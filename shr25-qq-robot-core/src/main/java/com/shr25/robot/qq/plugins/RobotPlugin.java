package com.shr25.robot.qq.plugins;

import com.shr25.robot.base.DelayTask;
import com.shr25.robot.common.Command;
import com.shr25.robot.common.ExecuteMessage;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.common.RobotMsgType;
import com.shr25.robot.qq.model.QqMessage;
import lombok.Data;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

import static com.shr25.robot.common.RobotMsgType.Group_Member;

/**
 * 机器人插件接口
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Data
public abstract class RobotPlugin {
    /** 日志打印 */
    protected Logger log = LoggerFactory.getLogger(getClass());;

    /** id */
    private Long id;

    /** 名称 */
    private String name;

    /** 描述 */
    private String desc = "";

    /** 默认排序 */
    private Integer sort = 10000;

    /** 是否启动 */
    private boolean enabled = true;

    /** 定时任务 */
    private DelayQueue<DelayTask> queue;

    /** 定时任务 */
    private Map<Long, DelayTask> delayTaskCache;

    /** 主要命令 */
    private Map<String, Command> masterCommands = new HashMap<>();

    /** 命令集 */
    private Map<String, Command> commands = new HashMap<>();

    public Collection<String> getAllMasterCommands() {
        Set<String> allCommands = new TreeSet<>();
        if(masterCommands.size() > 0){
            for (Command command : masterCommands.values()) {
                allCommands.add(command.getCommandStr() + "    " + command.getDesc());
            }
        }

        return allCommands;
    }

    public Set<String> getAllCommands(QqMessage qqMessage) {
        Set<String> allCommands = new TreeSet<>();
        if(commands.size() > 0){
            for (Command command : commands.values()) {
                allCommands.add(command.getCommandStr() + "    " + command.getDesc());
            }
        }

        return allCommands;
    }

    /**
     * 添加定时任务
     * @param groupId
     * @param time
     */
    public void addDelayQueue(Long groupId, Date time){
        addDelayQueue(groupId, time, null);
        log.info("添加定时器：{}", groupId);
    }


    public void addDelayQueue(Long groupId, Date time, Object data){
        boolean isRun = true;
        //没有延时队列，就创建
        if(queue == null){
            queue = new DelayQueue<>();
            delayTaskCache = new HashMap<>();
            isRun = false;
        }

        /** 监测是否已经存在 */
        DelayTask delayTask = delayTaskCache.get(groupId);
        //已存在就从队列中删除
        if(delayTask != null){
            removeDelayQueue(delayTask);
        }

        //添加队列
        delayTask = new DelayTask(groupId, time, data);
        queue.add(delayTask);
        //添加缓存
        delayTaskCache.put(groupId, delayTask);

        if(!isRun){
            log.info("==================<"+getName()+":定时器>==》启动 ");
            Executors.newFixedThreadPool(1).execute(new Thread(this::run));
        }
    }

    public void removeDelayQueue(DelayTask delayTask){
        queue.remove(delayTask);
    }

    /**
     * 启动定时器
     */
    private void run(){
        while (true){
            try{
                DelayTask task = queue.take();
                task(task.getGroupId(), task.getData());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行任务
     * @param groupId
     * @param data
     */
    protected void task(Long groupId, Object data){

    }

    /**
     * 获取插件描述
     * @return
     */
    public String info(){
        if(getCommands().size() > 0){
            StringBuffer info = new StringBuffer(desc+"\n指令列表:");
            int n = 0;
            for (Command command : getCommands().values()) {
                n++;
                info.append("\n" + n+ "、 "+command.getCommandStr());
            }
            return info.toString();
        }else{
            return desc;
        }
    }

    /**
     * 群组初始化插件
     * @param groupId
     */
    public void init(Long groupId){
    }

    /**
     * 群组取消插件
     * @param groupId
     */
    public void cancel(Long groupId){
    }

    /**
     * 添加描述信息，并末尾增加换行符
     *
     * @param desc 数据
     * @return 当前对象
     */
    public RobotPlugin addDescLn(String desc) {
        this.desc += desc + '\n';
        return this;
    }

    /**
     * 添加描述信息
     *
     * @param desc 数据
     * @return 当前对象
     */
    public RobotPlugin addDesc(String desc) {
        this.desc += desc;
        return this;
    }

    /**
     * 回复陌生人消息
     * @param qqMessage
     */
    public boolean executeStrangerMessage(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复好友消息
     * @param qqMessage
     */
    public boolean executeFriendMessage(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复群临时消息
     * @param qqMessage
     */
    public boolean executeGroupTempMessage(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复群消息
     * @param qqMessage
     */
    public boolean executeGroupMessage(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复群事件消息
     * @param qqMessage
     */
    public boolean executeGroupMember(QqMessage qqMessage){
        return true;
    }

    /**
     * 回复消息
     * @param qqMessage
     */
    public boolean executeMessage(QqMessage qqMessage){
        Boolean flag = false;
        switch (qqMessage.getRobotMsgType()){
            case Strange:
                flag = executeStrangerMessage(qqMessage);
                break;
            case Friend:
                flag = executeFriendMessage(qqMessage);
                break;
            case GroupTemp:
                flag = executeGroupTempMessage(qqMessage);
                break;
            case Group:
            case GroupAtBot:
                flag = executeGroupMessage(qqMessage);
                break;
        }
        return flag;
    }

    /**
     * 回复群消息
     * @param qqMessage
     */
    public boolean execute(QqMessage qqMessage){
        Boolean flag = false;
        switch (qqMessage.getRobotMsgType()){
            case Group_Member:
                flag = executeGroupMember(qqMessage);
                break;
            default:
                String commandStr = qqMessage.getCommand();
                Boolean isCommand = false;
                if(commandStr != null){
                    for (Command command : commands.values()) {
                        if(commandStr.equals(command.getCommandStr()) && command.getPermission().getPermission() >=  qqMessage.getRobotMsgPermission().getPermission()){
                            isCommand = true;
                            for (RobotMsgType robotMsgType: command.getRobotMsgTypes()){
                                if(robotMsgType == qqMessage.getRobotMsgType()){
                                    command.getExecuteMessage().executeMessage(qqMessage);
                                }
                            }
                        }
                    }
                }
                if(!isCommand){
                    flag = executeMessage(qqMessage);
                }
                break;
        }
        qqMessage.setExecuteNext(flag);
        return flag;
    }

    /**
     * 建造 MessageChain，参数是多个 SingleMessage
     *
     * @param m 多个 SingleMessage
     * @return 将多个 SingleMessage 组合成 MessageChain
     */
    protected static MessageChain buildMessageChain(Object... m) {
        MessageChainBuilder builder = new MessageChainBuilder();
        for (Object s : m) {
            if (s instanceof String) {
                s = new PlainText((CharSequence) s);
            }else if(s instanceof SingleMessage){
                builder.append((SingleMessage) s);
            }
        }
        return builder.build();
    }


    protected RobotPlugin addCommand(String command, String desc, Boolean... isMaster){
        addCommand(new Command(command, desc, qqMessage -> { return executeMessage(qqMessage);}), isMaster);
        return this;
    }

    /**
     * 添加命令
     * @param command
     * @param desc
     */
    protected RobotPlugin addCommand(String command, String desc, ExecuteMessage executeMessage, Boolean... isMaster) {
        addCommand(new Command(command, desc, executeMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     * @param command
     * @param desc
     */
    protected RobotPlugin addCommand(String command, String desc, RobotMsgPermission permission, ExecuteMessage executeMessage, Boolean... isMaster) {
        addCommand(new Command(command, desc, permission, executeMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     * @param command
     * @param desc
     */
    protected RobotPlugin addCommand(String command, String desc, RobotMsgType[] robotMsgTypes, ExecuteMessage executeMessage, Boolean... isMaster) {
        addCommand(new Command(command, desc, robotMsgTypes, executeMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     * @param command
     * @param desc
     */
    protected RobotPlugin addCommand(String command, String desc, RobotMsgPermission permission, RobotMsgType[] robotMsgTypes, ExecuteMessage executeMessage, Boolean... isMaster) {
        addCommand(new Command(command, desc, permission, robotMsgTypes, executeMessage), isMaster);
        return this;
    }

    /**
     * 添加命令
     * @param command
     * @param isMaster
     */
    protected void addCommand(Command command, Boolean... isMaster){
        if(isMaster != null && isMaster.length>0 && isMaster[0]){
            if(masterCommands.size()<2){
                masterCommands.put(command.getCommandStr(), command);
            }
        }
        commands.put(command.getCommandStr(), command);
    }
}
