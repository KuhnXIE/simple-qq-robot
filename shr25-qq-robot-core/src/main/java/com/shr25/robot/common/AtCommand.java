package com.shr25.robot.common;

import com.shr25.robot.qq.model.QqMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * at指令
 */
@Getter
@Setter
public class AtCommand {
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
    private RobotMsgPermission permission = RobotMsgPermission.ALL;

    /**
     * 默认接受消息
     */
    private RobotMsgType[] robotMsgTypes = {RobotMsgType.GroupAtBot, RobotMsgType.Group, RobotMsgType.Friend};

    private ExecuteAtMessage executeAtMessage;

    public AtCommand(String commandStr, String desc, ExecuteAtMessage executeAtMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.executeAtMessage = executeAtMessage;
    }

    public AtCommand(String commandStr, String desc, RobotMsgPermission permission, ExecuteAtMessage executeAtMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.permission = permission;
        this.executeAtMessage = executeAtMessage;
    }

    public AtCommand(String commandStr, String desc, RobotMsgType[] robotMsgTypes, ExecuteAtMessage executeAtMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.robotMsgTypes = robotMsgTypes;
        this.executeAtMessage = executeAtMessage;
    }

    public AtCommand(String commandStr, String desc, RobotMsgPermission permission, RobotMsgType[] robotMsgTypes, ExecuteAtMessage executeAtMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.permission = permission;
        this.robotMsgTypes = robotMsgTypes;
        this.executeAtMessage = executeAtMessage;
    }

    /**
     * 回复群事件消息
     * @param qqMessage
     */
    public boolean executeGroupMember(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复群消息
     */
    public void execute(QqMessage qqMessage){
        if(this.getPermission().getPermission() >=  qqMessage.getRobotMsgPermission().getPermission()){
            for (RobotMsgType robotMsgType: this.getRobotMsgTypes()){
                if(robotMsgType == qqMessage.getRobotMsgType()){
                    this.getExecuteAtMessage().executeMessage(qqMessage);
                }
            }
        }
    }
}
