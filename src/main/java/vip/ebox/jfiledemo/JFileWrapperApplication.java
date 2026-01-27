package vip.ebox.jfiledemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class JFileWrapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(JFileWrapperApplication.class, args);
    }

}
