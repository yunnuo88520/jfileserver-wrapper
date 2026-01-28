package vip.ebox.jfiledemo.service;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.filesys.server.auth.*;
import org.filesys.server.config.GlobalConfigSection;
import org.filesys.server.config.ServerConfiguration;
import org.filesys.server.config.CoreServerConfigSection;
import org.filesys.server.config.SecurityConfigSection;
import org.filesys.smb.DialectSelector;
import org.filesys.smb.server.SMBConfigSection;
import org.filesys.smb.server.SMBServer;
import org.filesys.server.filesys.DiskDeviceContext;
import org.filesys.server.filesys.DiskSharedDevice;
import org.filesys.server.filesys.FilesystemsConfigSection;
import org.filesys.smb.server.SMBSrvSession;
import org.filesys.smb.server.disk.JavaNIODiskDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Yunnuo
 * @Email: ymz@ebox.vip
 * @Date: 2026/1/27  13:37
 * @Description: jFileServer服务管理类
 */
@Slf4j
@Service
public class JFileServerService {

    @Autowired
    private vip.ebox.jfiledemo.config.JFileServerProperties properties;

    // jFileServer配置和服务器实例
    private ServerConfiguration serverConfig;
    private SMBServer smbServer;
    private final ReentrantLock lock = new ReentrantLock();

    // 服务器状态
    private volatile boolean running = false;

    /**
     * 启动jFileServer
     */
    public void start() {
        lock.lock();
        try {
            if (running) {
                log.warn("jFileServer已经在运行中，无需重复启动");
                return;
            }

            log.info("========== 开始启动jFileServer ==========");
            log.info("配置信息:");
            log.info("  - 端口: {}", properties.getPort());
            log.info("  - 共享目录: {}", properties.getSharePath());
            log.info("  - 共享名称: {}", properties.getShareName());
            log.info("  - 服务器名称: {}", properties.getServerName());
            log.info("  - 域名/工作组: {}", properties.getDomain());
            log.info("  - 用户名: {}", properties.getUsername());

            try {
                // 1. 创建服务器配置
                serverConfig = new ServerConfiguration(properties.getServerName());

                // 2. 添加全局配置
                GlobalConfigSection globalConfig =
                    new GlobalConfigSection(serverConfig);
                serverConfig.addConfigSection(globalConfig);

                // 3. 添加核心服务器配置
                CoreServerConfigSection coreConfig = new CoreServerConfigSection(serverConfig);

                // 配置内存池
                int[] pktSizes = {256, 4096, 16384, 66000};
                int[] initAlloc = {20, 20, 5, 5};
                int[] maxAlloc = {100, 50, 50, 50};
                coreConfig.setMemoryPool(pktSizes, initAlloc, maxAlloc);

                // 配置线程池
                coreConfig.setThreadPool(properties.getMinThreads(), properties.getMaxThreads());
                log.info("配置线程池: 最小={}, 最大={}", properties.getMinThreads(), properties.getMaxThreads());

                serverConfig.addConfigSection(coreConfig);

                // 3. 创建安全配置并添加用户
                SecurityConfigSection securityConfig = new SecurityConfigSection(serverConfig);

                // 创建用户账户列表并添加用户
                UserAccountList userAccounts = new UserAccountList();
                UserAccount userAccount = new UserAccount(
                    properties.getUsername(),
                    properties.getPassword()
                );
                userAccount.setAdministrator(true);

                // 关键：为NTLM认证生成MD4密码哈希
                try {
                    // 注册BouncyCastle安全提供者以支持MD4
                    Security.addProvider(new BouncyCastleProvider());

                    PasswordEncryptor encryptor = new PasswordEncryptor();
                    byte[] md4Hash = encryptor.generateEncryptedPassword(
                        properties.getPassword(),
                        null,
                        org.filesys.server.auth.PasswordEncryptor.MD4,
                        null,
                        null
                    );
                    userAccount.setMD4Password(md4Hash);
                    log.info("已为用户 {} 生成MD4密码哈希", properties.getUsername());
                } catch (Exception e) {
                    log.warn("生成MD4密码哈希失败: {}", e.getMessage());
                }

                userAccounts.addUser(userAccount);
                securityConfig.setUserAccounts(userAccounts);

                serverConfig.addConfigSection(securityConfig);
                log.info("已配置用户认证: {}", properties.getUsername());

                // 4. 创建SMB配置
                SMBConfigSection smbConfig = new SMBConfigSection(serverConfig);
                smbConfig.setServerName(properties.getServerName());
                smbConfig.setDomainName(properties.getDomain());

                // *** 关键配置：禁用空闲会话清理器，防止连接被自动关闭 ***
                // socketTimeout 设置为 0 或负数时，IdleSessionReaper 不会启动
                // 这样可以确保长时间挂载的连接（如ISO镜像安装）不会被断开
                smbConfig.setSocketTimeout(properties.getSocketTimeout());

                // 检查是否禁用了超时
                if (properties.getSocketTimeout() <= 0) {
                    log.info("已禁用 socket 超时，IdleSessionReaper 将不会启动，连接将保持稳定");
                } else {
                    log.info("已配置 socket 超时: {} 毫秒（空闲会话将在 {} 毫秒后被清理）",
                        properties.getSocketTimeout(), properties.getSocketTimeout() / 2);
                }

                // 配置SMB dialect - 支持SMB1（重要：很多客户端只支持SMB1）
                DialectSelector dialects = smbConfig.getEnabledDialects();
                dialects.AddDialect(org.filesys.smb.Dialect.NT);
                dialects.AddDialect(org.filesys.smb.Dialect.LanMan2);
                dialects.AddDialect(org.filesys.smb.Dialect.LanMan2_1);

                // 创建并设置认证器 - 使用EnterpriseSMBAuthenticator以支持NTLMv2
                //NTLMOnlySMBAuthenticator authenticator = new NTLMOnlySMBAuthenticator();
                EnterpriseSMBAuthenticator authenticator = new EnterpriseSMBAuthenticator();

                // 关键：必须手动设置authenticator的配置引用，否则无法获取用户信息
                authenticator.setConfig(serverConfig);

                authenticator.setAccessMode(SMBAuthenticator.AuthMode.USER);
                authenticator.setAllowGuest(true);  // 允许guest访问（免密）
                smbConfig.setAuthenticator(authenticator);
                log.info("已使用EnterpriseSMBAuthenticator，支持NTLMv1和NTLMv2认证");

                // 启用调试日志以便排查问题
                smbConfig.setSessionDebugFlags(EnumSet.of(
                        SMBSrvSession.Dbg.NEGOTIATE,
                        SMBSrvSession.Dbg.STATE,
                        SMBSrvSession.Dbg.SOCKET));

                // 设置端口和协议
                smbConfig.setTcpipSMB(true);

                smbConfig.setTcpipSMBPort(properties.getPort());
                // 设置bind address为0.0.0.0，允许所有接口连接
                try {
                    smbConfig.setSMBBindAddress(InetAddress.getByName("0.0.0.0"));
                } catch (UnknownHostException e) {
                    log.warn("无法设置bind address: {}", e.getMessage());
                }
                log.info("已启用TCP/IP SMB，端口: {}", properties.getPort());

                // 禁用NetBIOS SMB(跨平台兼容性更好)
                smbConfig.setNetBIOSSMB(false);
                log.info("禁用NetBIOS SMB");


                serverConfig.addConfigSection(smbConfig);

                // 5. 创建文件系统配置
                FilesystemsConfigSection filesystemsConfig = new FilesystemsConfigSection(serverConfig);
                serverConfig.addConfigSection(filesystemsConfig);

                // 处理共享目录路径
                File sharedDir;
                String sharePath = properties.getSharePath();
                if (new File(sharePath).isAbsolute()) {
                    sharedDir = new File(sharePath);
                } else {
                    // 相对路径，相对于项目根目录
                    sharedDir = new File(System.getProperty("user.dir"), sharePath);
                }

                // 确保共享目录存在
                if (!sharedDir.exists()) {
                    log.info("共享目录不存在，正在创建: {}", sharedDir.getAbsolutePath());
                    if (sharedDir.mkdirs()) {
                        log.info("共享目录创建成功: {}", sharedDir.getAbsolutePath());
                    } else {
                        log.error("共享目录创建失败: {}", sharedDir.getAbsolutePath());
                        throw new RuntimeException("无法创建共享目录: " + sharedDir.getAbsolutePath());
                    }
                }

                log.info("共享目录: {}", sharedDir.getAbsolutePath());

                // 创建磁盘设备接口和上下文
                JavaNIODiskDriver diskDriver = new JavaNIODiskDriver();
                DiskDeviceContext diskContext = new DiskDeviceContext(
                    sharedDir.getAbsolutePath(),
                    properties.getShareName()
                );

                // 创建磁盘共享设备
                DiskSharedDevice diskShare = new DiskSharedDevice(
                    properties.getShareName(),
                    diskDriver,
                    diskContext
                );

                // 添加共享到文件系统配置
                filesystemsConfig.addShare(diskShare);
                log.info("已添加共享: {} -> {}", properties.getShareName(), sharedDir.getAbsolutePath());

                // 6. 创建并启动SMB服务器
                smbServer = new SMBServer(serverConfig);
                log.info("正在启动SMB服务器...");
                smbServer.startServer();

                // 等待服务器启动
                Thread.sleep(1000);

                if (smbServer.isActive()) {
                    running = true;
                    log.info("========== jFileServer启动成功 ==========");
                } else {
                    log.error("jFileServer启动失败，服务器未处于活动状态");
                    throw new RuntimeException("SMB服务器启动失败");
                }

            } catch (InterruptedException e) {
                log.error("启动jFileServer时被中断", e);
                Thread.currentThread().interrupt();
                // 清理资源
                cleanup();
                throw new RuntimeException("启动jFileServer失败: " + e.getMessage(), e);
            } catch (Exception e) {
                log.error("启动jFileServer时发生错误", e);
                // 清理资源
                cleanup();
                throw new RuntimeException("启动jFileServer失败: " + e.getMessage(), e);
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * 停止jFileServer
     */
    public void stop() {
        lock.lock();
        try {
            if (!running) {
                log.warn("jFileServer未运行，无需停止");
                return;
            }

            log.info("========== 开始停止jFileServer ==========");

            try {
                cleanup();
                running = false;
                log.info("========== jFileServer已停止 ==========");
            } catch (Exception e) {
                log.error("停止jFileServer时发生错误", e);
                throw new RuntimeException("停止jFileServer失败: " + e.getMessage(), e);
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * 重启jFileServer
     */
    public void restart() {
        log.info("========== 开始重启jFileServer ==========");
        stop();
        try {
            Thread.sleep(2000); // 等待2秒确保完全停止
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
        log.info("========== jFileServer重启完成 ==========");
    }

    /**
     * 获取服务器状态
     */
    public ServerStatus getStatus() {
        boolean isActive = smbServer != null && smbServer.isActive();
        return new ServerStatus(running, isActive, properties.getPort(),
                properties.getShareName(), properties.getServerName());
    }

    /**
     * 检查服务器是否正在运行
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        try {
            if (smbServer != null) {
                log.info("正在关闭SMB服务器...");
                smbServer.shutdownServer(false);
                smbServer = null;
            }

            if (serverConfig != null) {
                log.info("正在关闭服务器配置...");
                serverConfig.closeConfiguration();
                serverConfig = null;
            }

        } catch (Exception e) {
            log.error("清理资源时发生错误", e);
        }
    }

    /**
     * 应用关闭时的清理方法
     */
    @PreDestroy
    public void onDestroy() {
        if (running) {
            log.info("应用正在关闭，自动停止jFileServer...");
            stop();
        }
    }

    /**
     * 服务器状态信息类
     */
    public static class ServerStatus {
        private final boolean running;
        private final boolean active;
        private final int port;
        private final String shareName;
        private final String serverName;

        public ServerStatus(boolean running, boolean active, int port,
                           String shareName, String serverName) {
            this.running = running;
            this.active = active;
            this.port = port;
            this.shareName = shareName;
            this.serverName = serverName;
        }

        public boolean isRunning() {
            return running;
        }

        public boolean isActive() {
            return active;
        }

        public int getPort() {
            return port;
        }

        public String getShareName() {
            return shareName;
        }

        public String getServerName() {
            return serverName;
        }

        @Override
        public String toString() {
            return "ServerStatus{" +
                    "running=" + running +
                    ", active=" + active +
                    ", port=" + port +
                    ", shareName='" + shareName + '\'' +
                    ", serverName='" + serverName + '\'' +
                    '}';
        }
    }
}
