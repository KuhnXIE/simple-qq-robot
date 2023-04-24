package com.shr25.robot.common;

/**
 * 机器人命令权限级别
 */
public enum RobotMsgPermission {
    ALL(99),
    MEMBER(4),
    ADMINISTRATOR(3),
    OWNER(2),
    ADMIN(1),
    SYSTEM(0);

    private int permission;

    RobotMsgPermission(int permission){
        this.permission = permission;
    }

    public int getPermission(){
        return this.permission;
    }
}
