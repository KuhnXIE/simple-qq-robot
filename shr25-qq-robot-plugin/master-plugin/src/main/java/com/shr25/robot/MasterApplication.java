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
public class MasterApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(MasterApplication.class, args);
        Thread.currentThread().join();
    }
}
