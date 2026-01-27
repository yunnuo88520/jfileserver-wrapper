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
}
