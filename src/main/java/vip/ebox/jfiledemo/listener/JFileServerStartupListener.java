package vip.ebox.jfiledemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import vip.ebox.jfiledemo.config.JFileServerProperties;
import vip.ebox.jfiledemo.service.JFileServerService;

/**
 * @Author: Yunnuo
 * @Email: ymz@ebox.vip
 * @Date: 2026/1/27  16:03
 * @Description: jFileServer启动监听器
 *  在Spring Boot应用启动完成后自动启动jFileServer
 */
@Slf4j
@Component
@Order(1) // 确保在其他组件之后执行
public class JFileServerStartupListener implements ApplicationRunner {

    @Autowired
    private JFileServerService jFileServerService;

    @Autowired
    private JFileServerProperties properties;

    @Override
    public void run(ApplicationArguments args) {

        if (!properties.isAutoStart()) {
            log.info("jFileServer未配置自动启动，如需启动请调用API接口");
            return;
        }

        try {
            log.info("========================================");
            log.info("应用启动完成，准备自动启动jFileServer...");
            log.info("========================================");

            // 延迟1秒启动，确保Spring容器完全初始化
            Thread.sleep(1000);

            jFileServerService.start();

            log.info("jFileServer自动启动完成！");
            log.info("========================================");


        } catch (Exception e) {
            log.error("jFileServer自动启动失败", e);
            log.error("您可以稍后通过API接口手动启动jFileServer");
        }
    }
}
