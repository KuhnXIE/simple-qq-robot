package com.shr25.robot.base;

import com.shr25.robot.common.SimCommand;
import com.shr25.robot.common.ExecuteSimMessage;
import com.shr25.robot.common.RobotMsgPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 普通指令抽象类
 */
public abstract interface BaseAbstractSim {

    /** 日志打印 */
    Logger log = LoggerFactory.getLogger(BaseAbstractSim.class);;

    /** 管理员命令 */
    public static Map<String, SimCommand> masterCommands = new HashMap<>();

    /** 命令集 */
    public static Map<String, SimCommand> commands = new HashMap<>();

    default Map<String, SimCommand> getCommands(){
        return commands;
    }

    /**
     * 添加命令
     */
    default BaseAbstractSim addCommand(String command, String desc, String classify, ExecuteSimMessage executeSimMessage, Boolean... isMaster) {
        addCommand(new SimCommand(command, desc, classify, executeSimMessage), isMaster);
        return this;
    }


    /**
     * 添加命令
     */
    default BaseAbstractSim addCommand(String command, String desc, String classify, RobotMsgPermission permission, ExecuteSimMessage executeSimMessage, Boolean... isMaster) {
        addCommand(new SimCommand(command, desc, classify, permission, executeSimMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     */
    default BaseAbstractSim addCommand(SimCommand command, Boolean... isMaster){
        if(isMaster != null && isMaster.length>0 && isMaster[0]){
            if(masterCommands.size()<2){
                masterCommands.put(command.getCommandStr(), command);
            }
        }
        commands.put(command.getCommandStr(), command);
        return this;
    }

    String getName();
    
}
