package com.shr25.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动类
 *
 * @author huobing
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(DemoApplication.class, args);
        Thread.currentThread().join();
    }
}
