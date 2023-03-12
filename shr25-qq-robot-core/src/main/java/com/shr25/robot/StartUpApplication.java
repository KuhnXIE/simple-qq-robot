package com.shr25.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author huobing
 */
@SpringBootApplication
public class StartUpApplication {

    public static void main(String[] args) throws Exception {
       SpringApplication.run(StartUpApplication.class, args);
        Thread.currentThread().join();
    }
}
