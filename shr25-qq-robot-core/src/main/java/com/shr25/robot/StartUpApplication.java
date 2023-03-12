package com.shr25.robot;

import com.shr25.robot.protocol.FixProtocolVersion;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * 启动类
 *
 * @author huobing
 */
@SpringBootApplication
public class StartUpApplication {

    public static void main(String[] args) throws Exception {
        FixProtocolVersion.update();
        Map<BotConfiguration.MiraiProtocol, String> miraiProtocol = FixProtocolVersion.info();
        SpringApplication.run(StartUpApplication.class, args);
        Thread.currentThread().join();
    }
}
