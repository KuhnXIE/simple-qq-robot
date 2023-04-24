package com.shr25.robot.common;

/**
 * 机器人命令权限级别
 */

public enum RobotMsgType {
    ALL(99), //所有类型消息
    GroupAtBot(5), //群at机器人消息
    Group(4), //群消息
    GroupTemp(3), //群临时消息
    Friend(2),  //好友消息
    Strange(1), //陌生人消息
    Group_Member(0); //群系统消息

    private int permission;

    RobotMsgType(int permission){
        this.permission = permission;
    }

    public int getPermission(){
        return this.permission;
    }
}
