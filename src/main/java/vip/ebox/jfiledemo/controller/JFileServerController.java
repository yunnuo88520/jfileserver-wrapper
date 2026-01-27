package vip.ebox.jfiledemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.ebox.jfiledemo.service.JFileServerService;
import vip.ebox.jfiledemo.config.JFileServerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yunnuo
 * @Email: ymz@ebox.vip
 * @Date: 2026/1/27  15:24
 * @Description: jFileServer管理控制器
 *  提供启动、停止、重启、状态查询等API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/jfileserver")
public class JFileServerController {

    @Autowired
    private JFileServerService jFileServerService;

    @Autowired
    private JFileServerProperties properties;

    /**
     * 启动jFileServer
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start() {
        log.info("收到启动jFileServer的API请求");
        Map<String, Object> result = new HashMap<>();

        try {
            /*if (!properties.isEnabled()) {
                result.put("success", false);
                result.put("message", "jFileServer未启用，请在配置文件中设置jfileserver.enabled=true");
                return ResponseEntity.badRequest().body(result);
            }*/

            jFileServerService.start();
            result.put("success", true);
            result.put("message", "jFileServer启动成功");
            result.put("status", jFileServerService.getStatus());
            log.info("jFileServer启动API调用成功");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("启动jFileServer失败", e);
            result.put("success", false);
            result.put("message", "启动失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 停止jFileServer
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stop() {
        log.info("收到停止jFileServer的API请求");
        Map<String, Object> result = new HashMap<>();

        try {
            jFileServerService.stop();
            result.put("success", true);
            result.put("message", "jFileServer已停止");
            result.put("status", jFileServerService.getStatus());
            log.info("jFileServer停止API调用成功");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("停止jFileServer失败", e);
            result.put("success", false);
            result.put("message", "停止失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 重启jFileServer
     */
    @PostMapping("/restart")
    public ResponseEntity<Map<String, Object>> restart() {
        log.info("收到重启jFileServer的API请求");
        Map<String, Object> result = new HashMap<>();

        try {
            /*if (!properties.isEnabled()) {
                result.put("success", false);
                result.put("message", "jFileServer未启用，请在配置文件中设置jfileserver.enabled=true");
                return ResponseEntity.badRequest().body(result);
            }*/

            jFileServerService.restart();
            result.put("success", true);
            result.put("message", "jFileServer重启成功");
            result.put("status", jFileServerService.getStatus());
            log.info("jFileServer重启API调用成功");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("重启jFileServer失败", e);
            result.put("success", false);
            result.put("message", "重启失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 查询jFileServer状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        log.debug("收到查询jFileServer状态的API请求");
        Map<String, Object> result = new HashMap<>();

        try {
            JFileServerService.ServerStatus status = jFileServerService.getStatus();
            result.put("success", true);
            result.put("data", status);
            result.put("autoStart", properties.isAutoStart());

            // 添加配置信息
            Map<String, Object> config = new HashMap<>();
            config.put("port", properties.getPort());
            config.put("sharePath", properties.getSharePath());
            config.put("shareName", properties.getShareName());
            config.put("serverName", properties.getServerName());
            config.put("domain", properties.getDomain());
            config.put("username", properties.getUsername());
            result.put("config", config);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("查询状态失败", e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 获取配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        log.debug("收到获取jFileServer配置的API请求");
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("success", true);
            result.put("data", properties);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("获取配置失败", e);
            result.put("success", false);
            result.put("message", "获取配置失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        JFileServerService.ServerStatus status = jFileServerService.getStatus();

        result.put("status", status.isRunning() ? "UP" : "DOWN");
        result.put("active", status.isActive());

        return ResponseEntity.ok(result);
    }
}
