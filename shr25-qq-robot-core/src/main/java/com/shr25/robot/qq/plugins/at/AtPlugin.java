package com.shr25.robot.qq.plugins.at;

import com.shr25.robot.common.AtCommand;
import com.shr25.robot.common.ExecuteAtMessage;
import com.shr25.robot.common.RobotMsgPermission;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class AtPlugin {

    /** 日志打印 */
    protected Logger log = LoggerFactory.getLogger(getClass());;

    /** 名称 */
    private String name;

    /** 描述 */
    private String desc = "";
    
    /** 管理员命令 */
    private Map<String, AtCommand> masterCommands = new HashMap<>();

    /** 命令集 */
    private Map<String, AtCommand> commands = new HashMap<>();

    /**
     * 添加描述信息
     *
     * @param desc 数据
     * @return 当前对象
     */
    public AtPlugin addDesc(String desc) {
        this.desc += desc;
        return this;
    }
    
    /**
     * 添加命令
     */
    protected AtPlugin addCommand(String command, String desc, ExecuteAtMessage executeAtMessage, Boolean... isMaster) {
        addCommand(new AtCommand(command, desc, executeAtMessage), isMaster);
        return this;
    }


    /**
     * 添加命令
     */
    protected AtPlugin addCommand(String command, String desc, RobotMsgPermission permission, ExecuteAtMessage executeAtMessage, Boolean... isMaster) {
        addCommand(new AtCommand(command, desc, permission, executeAtMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     */
    protected void addCommand(AtCommand command, Boolean... isMaster){
        if(isMaster != null && isMaster.length>0 && isMaster[0]){
            if(masterCommands.size()<2){
                masterCommands.put(command.getCommandStr(), command);
            }
        }
        commands.put(command.getCommandStr(), command);
    }

}
