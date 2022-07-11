package com.shr25.robot.qq.enums;

import java.util.Arrays;

/**
 * 机器人事件枚举
 *
 * @author huobing
 */
public enum RobotEventEnum {
    /**
     * 群成员的临时消息
     */
    TEMP_MSG,

    /**
     * 好友消息
     */
    FRIEND_MSG,

    /**
     * 群消息
     */
    GROUP_MSG,

    /**
     * 群成员的临时消息
     */
    GROUP_TEMP_MSG,

    /**
     * 申请入群
     */
    MEMBER_JOIN_REQUEST,

    /**
     * 群成员已经加群
     */
    MEMBER_JOIN,

    /**
     * 成员主动离开群
     */
    MEMBER_LEAVE,

    /**
     * 消息撤回
     */
    RECALL_EVENT;

    /**
     * 机器人消息类型的事件枚举
     */
    public static final RobotEventEnum[] robotMessageEventEnums = new RobotEventEnum[]{
            FRIEND_MSG, GROUP_MSG, TEMP_MSG
    };

    /**
     * 判断枚举是否为消息类型的事件
     */
    public boolean isMessageEvent() {
        return Arrays.stream(robotMessageEventEnums).anyMatch(robotEventEnum -> robotEventEnum.equals(this));
    }

    /**
     * 是否群消息
     */
    public boolean isGroupMsg() {
        return GROUP_MSG.equals(this);
    }

    /**
     * 是否退群消息
     */
    public boolean isMemberLeave() {
        return MEMBER_LEAVE.equals(this);
    }

    /**
     * 是否好友消息
     */
    public boolean isFriendMsg() {
        return FRIEND_MSG.equals(this);
    }
}