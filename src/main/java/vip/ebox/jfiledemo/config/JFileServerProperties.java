package vip.ebox.jfiledemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Author: Yunnuo
 * @Email: ymz@ebox.vip
 * @Date: 2026/1/27  13:36
 * @Description: jFileServer配置属性类
 *  从application.yml中读取jFileServer相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jfileserver")
public class JFileServerProperties {

    /**
     * 是否在应用启动时自动启动jFileServer
     */
    private boolean autoStart = true;

    /**
     * SMB/CIFS端口
     */
    private int port = 47531;

    /**
     * 共享目录（支持相对路径和绝对路径）
     */
    private String sharePath = "share";

    /**
     * 共享名称
     */
    private String shareName = "JFILESHARE";

    /**
     * 服务器名称
     */
    private String serverName = "JFileServer";

    /**
     * 域名/工作组
     */
    private String domain = "FILESRV";

    /**
     * 用户名
     */
    private String username = "admin";

    /**
     * 密码
     */
    private String password = "jfilesrv";

    /**
     * 是否启用调试日志
     */
    private boolean debug = false;

    /**
     * 最小线程数
     */
    private int minThreads = 5;
    /**
     * 最大线程数
     */
    private int maxThreads = 20;

    /**
     * Socket 超时时间（毫秒）
     * 0 或负数表示禁用超时，连接不会被自动关闭
     *
     * 推荐设置为 0，原因：
     * 1. 禁用后 IdleSessionReaper（空闲会话清理器）不会启动
     * 2. 连接将保持稳定，不会因为空闲被自动关闭
     * 3. 避免长时间操作（如挂载ISO安装系统、大文件传输）时连接断开
     *
     * 如果设置为正值（如 900000 = 15分钟）：
     * - 空闲会话将在 timeout/2 时间后被自动清理
     * - 例如：设置 900000，空闲 7.5 分钟后连接会被关闭
     */
    private int socketTimeout = 0;

    /**
     * jFileServer 日志文件路径
     * 支持相对路径和绝对路径
     * - 如果为 null 或空字符串，则输出到控制台
     * - 相对路径相对于项目根目录
     *
     * 示例：
     * - logs/jfileserver.log（相对路径，推荐）
     * - /var/log/jfileserver/jfileserver.log（绝对路径）
     * - ""（空字符串，输出到控制台）
     */
    private String logFilePath = "logs/jfileserver.log";

    /**
     * 日志是否追加
     */
    private boolean logAppend = true;
}
