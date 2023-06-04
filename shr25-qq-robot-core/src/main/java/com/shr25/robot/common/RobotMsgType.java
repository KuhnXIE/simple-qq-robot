package com.shr25.robot.common;

/**
 * 消息类型
 */

public enum RobotMsgType {
    /** 所有类型消息 */
    ALL(99),
    /** 群at机器人消息 */
    GroupAtBot(5),
    /** 群普通消息 */
    Group(4),
    /** 群临时消息 */
    GroupTemp(3),
    /** 好友消息 */
    Friend(2),
    /** 陌生人消息 */
    Strange(1),
    /** 群系统消息 */
    Group_Member(0);

    private int msgType;

    RobotMsgType(int msgType){
        this.msgType = msgType;
    }

    public int getMsgType(){
        return this.msgType;
    }
}
