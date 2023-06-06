package com.shr25.robot.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 指令
 */
@Getter
@Setter
public class Command {
    /**
     * 指令
     */
    private String commandStr;

    /**
     * 描述
     */
    private String desc;

    /**
     * 最小权限
     */
    private RobotMsgPermission permission = RobotMsgPermission.ADMINISTRATOR;

    /**
     * 默认接受消息
     */
    private RobotMsgType[] robotMsgTypes = {RobotMsgType.GroupAtBot, RobotMsgType.Group, RobotMsgType.Friend};

    private ExecuteMessage executeMessage;

    public Command(String commandStr, String desc, ExecuteMessage executeMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.executeMessage = executeMessage;
    }

    public Command(String commandStr, String desc, RobotMsgPermission permission, ExecuteMessage executeMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.permission = permission;
        this.executeMessage = executeMessage;
    }

    public Command(String commandStr, String desc, RobotMsgType[] robotMsgTypes, ExecuteMessage executeMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.robotMsgTypes = robotMsgTypes;
        this.executeMessage = executeMessage;
    }

    public Command(String commandStr, String desc, RobotMsgPermission permission, RobotMsgType[] robotMsgTypes, ExecuteMessage executeMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.permission = permission;
        this.robotMsgTypes = robotMsgTypes;
        this.executeMessage = executeMessage;
    }
}
