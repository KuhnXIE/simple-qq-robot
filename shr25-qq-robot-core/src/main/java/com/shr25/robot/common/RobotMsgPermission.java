package com.shr25.robot.common;

/**
 * 机器人命令权限级别
 */
public enum RobotMsgPermission {
    ALL(99),
    /** 普通用户 */
    MEMBER(4),
    /** 群管理 */
    ADMINISTRATOR(3),
    /** 群主 */
    OWNER(2),
    /** 管理员 */
    ADMIN(1),
    /** 机器人管理员 */
    SYSTEM(0),
    /** QQ系统消息 */
    QQ_SYSTEM(-1);

    private int permission;

    RobotMsgPermission(int permission){
        this.permission = permission;
    }

    public int getPermission(){
        return this.permission;
    }
}
