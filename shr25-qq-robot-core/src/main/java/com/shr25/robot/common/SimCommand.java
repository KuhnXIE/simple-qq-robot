package com.shr25.robot.common;

import com.shr25.robot.qq.model.QqMessage;
import lombok.Data;

/**
 * 普通指令
 */
@Data
public class SimCommand {

    /**
     * 指令
     */
    private String commandStr;

    /**
     * 描述
     */
    private String desc;

    /**
     * 分类
     */
    private String classify;

    /**
     * 最小权限
     */
    private RobotMsgPermission permission = RobotMsgPermission.ALL;

    /**
     * 默认接受消息
     */
    private RobotMsgType[] robotMsgTypes = {RobotMsgType.GroupAtBot, RobotMsgType.Group, RobotMsgType.Friend};

    private ExecuteSimMessage executeSimMessage;

    public SimCommand(String commandStr, String desc, String classify, ExecuteSimMessage executeSimMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.classify = classify;
        this.executeSimMessage = executeSimMessage;
    }

    public SimCommand(String commandStr, String desc, String classify, RobotMsgPermission permission, ExecuteSimMessage executeSimMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.classify = classify;
        this.permission = permission;
        this.executeSimMessage = executeSimMessage;
    }

    public SimCommand(String commandStr, String desc, String classify, RobotMsgType[] robotMsgTypes, ExecuteSimMessage executeSimMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.classify = classify;
        this.robotMsgTypes = robotMsgTypes;
        this.executeSimMessage = executeSimMessage;
    }

    public SimCommand(String commandStr, String desc, String classify, RobotMsgPermission permission, RobotMsgType[] robotMsgTypes, ExecuteSimMessage executeSimMessage) {
        this.commandStr = commandStr;
        this.desc = desc;
        this.classify = classify;
        this.permission = permission;
        this.robotMsgTypes = robotMsgTypes;
        this.executeSimMessage = executeSimMessage;
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
                    this.getExecuteSimMessage().executeMessage(qqMessage);
                }
            }
        }
    }

}
