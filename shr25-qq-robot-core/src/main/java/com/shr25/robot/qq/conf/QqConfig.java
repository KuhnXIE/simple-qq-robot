package com.shr25.robot.qq.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 项目配置类
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Component
@ConfigurationProperties(value = "project.qq-robot")
@Data
public class QqConfig {
    /** 机器人QQ的名称 */
    private String name = "树人";

    /** 机器人QQ号 */
    private Long qq;
    /** 机器人密码 */
    private String password;

    /** 是否自动登录 */
    private boolean autoLogin = true;

    /** 是否扫码登录 */
    private boolean loginByQr = false;

    /** 使用协议 ANDROID_PHONE,  ANDROID_PAD, ANDROID_WATCH, IPAD, MACOS */
    private String protocol;

    /** 机器人的工作空间 */
    private String workspace;

    /** 是否输出机器人日志 */
    private boolean logOut;

    /** 简化命令 true进入推荐命令模式  false 全部命令模式*/
    private boolean simplifyCommand = false;

    /** 管理员qq列表 */
    private Set<Long> rootManageQq;
}
