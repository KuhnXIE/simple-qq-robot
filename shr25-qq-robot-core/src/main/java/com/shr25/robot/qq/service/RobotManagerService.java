package com.shr25.robot.qq.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.shr25.robot.api.QingYunKeApi;
import com.shr25.robot.base.BaseAbstractSim;
import com.shr25.robot.common.AtCommand;
import com.shr25.robot.common.SimCommand;
import com.shr25.robot.music.MusicCardProvider;
import com.shr25.robot.music.MusicFactory;
import com.shr25.robot.music.MusicInfo;
import com.shr25.robot.music.MusicSource;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqGroupPlugin;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.model.Vo.QqPluginVo;
import com.shr25.robot.qq.model.dict.AbstractDict;
import com.shr25.robot.qq.model.qqPlugin.QqPlugin;
import com.shr25.robot.qq.plugins.RobotPlugin;
import com.shr25.robot.qq.plugins.at.AtPlugin;
import com.shr25.robot.qq.plugins.at.ChatPlugin;
import com.shr25.robot.qq.service.qqGroup.IQqGroupPluginService;
import com.shr25.robot.qq.service.qqPlugin.IQqPluginService;
import com.shr25.robot.qq.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.Message;
import org.jetbrains.annotations.NotNull;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机器人服务类
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Service
@Slf4j
public class RobotManagerService {

    /** 默认QQ好友管理插件key */
    public static final Long FRIEND_QQ = 0L;

    private final String template = "名称：%s；启动：%s\n";

    @Autowired
    private IQqGroupPluginService qqGroupPluginService;

    @Autowired
    private IQqPluginService qqPluginService;

    @Autowired
    private QqConfig qqConfig;

    /** 是否开启 */
    private boolean enabled = true;

    /** 普通管理员qq列表 */
    private static final Set<Long> NORMAL_MANAGE_QQ = new HashSet<>();

    /** 根据群号隔离每个插件的启动 */
    private final Map<Long, List<QqPluginVo>> manages = new HashMap<>();

    /** 公共插件 */
    private List<QqPluginVo> publickPlugins;

    /** 所以插件 */
    private List<QqPluginVo> allPluginVos;

    /** 所有插件 */
    private List<QqPlugin> allPlugins;

    /** 缓存qq插件  插件id => 插件 */
    private final Map<Long, QqPlugin> qqPluginIdMap = new HashMap<>();

    /** 缓存qq插件  插件id => 插件 */
    private final Map<String, QqPlugin> qqPluginNameMap = new HashMap<>();

    /**
     * 艾特插件
     */
    private final Map<String, AtPlugin> atNamePluginMap = new HashMap<>();
    /**
     * 所有艾特命令的索引
     */
    private final Map<String, AtCommand> atCommondPluginMap = new HashMap<>();

    /**
     * 普通命令插件集合
     */
    private Map<String, List<SimCommand>> baseSimMap = new HashMap<>();
    /**
     * 所有普通命令的索引
     */
    private final Map<String, SimCommand> simCommandMap = new HashMap<>();

    /**
     * 指令描述
     */
    private final Map<String, String> atPluginDescMap = new HashMap<>();

    public static final String split = " ";

    /***************
     =======功能菜单=======
     签        到星        座
     新        闻礼        物
     📆黄        历天        气🌧️
     人        品点        歌
     配        对雇        佣🥳
     比        武结        婚
     花        墙灵        宠
     拆  盲  盒抽        奖
     🤝查  邀  请抽        签
     💖逼        婚抢        婚️‍
     🔮贵        族祈        福🧧
     领结婚证双        修
     👰🏻婚姻排行生  宝  宝
     排  行  榜猜  图  片
     王者荣耀猜  歌  名
     和平精英英雄联盟⭐
     舔狗日记历史今天
     成语接龙科举问答
     进群提醒小  黑  屋
     退群提醒黑  名  单
     新人欢迎群  空  间
     改名提醒C P  D D
     查有效期智能聊天
     群  指  令防  撤   回🎊
     🤡讲  笑  话查  活   跃️
     =================
     所有功能不用@我
     *******************/

    public String getDesc() {
        return "系统管理 使用方式：\n"
          + "#初始化  " + "#开机\n"
          + "#关机  " + "管理员列表\n"
          + "#添加管理员 {qq号/@群成员}\n"
          + "#删除管理员 {qq号/@群成员}\n"
          + "#群白名单\n"
          + "#添加群 {群号}\n"
          + "#删除群 {群号}\n"
          + "#机器人状态\n"
          + "#全部插件/#所有插件\n"
//          + "插件列表 {群号，管理员发群消息可以省略}\n"
//          + "添加插件 {插件名称} {群号，管理员发群消息可以省略} \n"
//          + "删除插件 {插件名称} {群号，管理员发群消息可以省略} \n"
          + "#开启插件 {插件名称}\n"
          + "#关闭插件 {插件名称}\n"
          + "#插件详情 {插件名称}\n";
    }

    public void publishMessage(Event event) {
        QqMessage qqMessage = new QqMessage(event, qqConfig, NORMAL_MANAGE_QQ);

        if (qqMessage.getRobotMsgType().getMsgType() > 1) {
            if (StringUtils.isNotBlank(qqMessage.getCommand())) {
                switch (qqMessage.getCommand()) {
                    case "help":
                    case "管理":
                        if (qqMessage.isManager()) {
                            qqMessage.putReplyMessage(getDesc());
                        }
                        break;
                    case "初始化":
                        if (qqMessage.isManager()) {
                            init();
                            qqMessage.putReplyMessage("初始化成功！~~~");
                        }
                        break;
                    case "开机":
                        if (qqMessage.isManager()) {
                            enabled = true;
                            this.addRobotStatusMessage(qqMessage);
                        }
                        break;
                    case "关机":
                        if (qqMessage.isManager()) {
                            enabled = false;
                            this.addRobotStatusMessage(qqMessage);
                        }
                        break;
                    case "管理员列表":
                        if (qqMessage.isManager()) {
                            this.addGroupListMessage(qqMessage);
                        }
                        break;
                    case "全部插件":
                    case "所有插件":
                        if (qqMessage.isManager()) {
                            this.addAllPluginsListMessage(qqMessage);
                        }
                        break;
                    case "插件列表":
                        if (StringUtils.isBlank(qqMessage.getContent())) {
                            this.addPluginsListMessage(qqMessage);
                        } else {
                            if (qqMessage.isManager()) {
                                this.addPluginsListMessage(qqMessage, qqMessage.getParameter());
                            }
                        }
                        break;
                    case "机器人状态":
                        if (qqMessage.isManager()) {
                            this.addRobotStatusMessage(qqMessage);
                        }
                        break;
                    case "添加管理员":
                        if (qqMessage.isSuperManager()) {
                            this.addManage(qqMessage, qqMessage.getParameter(), true);
                        }
                        break;
                    case "删除管理员":
                        if (qqMessage.isSuperManager()) {
                            this.addManage(qqMessage, qqMessage.getParameter(), false);
                        }
                        break;
                    case "添加群":
                        if (qqMessage.isManager()) {
                            this.addGroupList(qqMessage, qqMessage.getParameter(), true);
                        }
                        break;
                    case "删除群":
                        if (qqMessage.isManager()) {
                            this.addGroupList(qqMessage, qqMessage.getParameter(), false);
                        }
                        break;
                    case "添加插件":
                        if (qqMessage.isManager()) {
                            this.addPluginsList(qqMessage, qqMessage.getParameter(), true);
                        }
                        break;
                    case "删除插件":
                        if (qqMessage.isManager()) {
                            this.addPluginsList(qqMessage, qqMessage.getParameter(), false);
                        }
                        break;
                    case "开启插件":
                        if (qqMessage.isManager() || qqMessage.isCanOperatorGroup()) {
                            this.startPlugin(qqMessage, qqMessage.getParameter(), true);
                        }
                        break;
                    case "关闭插件":
                        if (qqMessage.isManager() || qqMessage.isCanOperatorGroup()) {
                            this.startPlugin(qqMessage, qqMessage.getParameter(), false);
                        }
                        break;
                    default:
                        //是否插件简介
                        boolean isPluginDesc = false;
                        if (StringUtils.isNotBlank(qqMessage.getCommand())) {
                            QqPlugin qqPlugin = qqPluginNameMap.get(qqMessage.getCommand());
                            if (qqPlugin != null) {
                                try {
                                    Class<RobotPlugin> clazz = (Class<RobotPlugin>) Class.forName(qqPlugin.getClassName());
                                    RobotPlugin robotPlugin = SpringUtil.getBean(clazz);
                                    qqMessage.putReplyMessage(robotPlugin.info());
                                    isPluginDesc = true;
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (!isPluginDesc) {
                            execute(qqMessage);
                        }
                        break;
                }
                // 如果是艾特消息，判断是不是管理员
            } else if (qqMessage.getAt()) {
/*                if (qqMessage.isManager() || qqMessage.isCanOperatorGroup()){
                    qqMessage.putReplyMessage(getCommandsStr(qqMessage));
                }*/
                executeAt(qqMessage);

                // 不是指令也不是艾特消息走这里
            } else {
                String content = qqMessage.getContent();
                if (StringUtils.isNotBlank(content)) {
                    String command = subCommand(content);
                    // 是普通指令
                    if (simCommandMap.containsKey(command)){
                        executeSim(qqMessage);
                    } else {
                        // 如果都不是扩展功能就走自定义的聊天功能
                        chatByLexicon(qqMessage);
                        // fixme 此处是最后一个设置消息的地方，其它命令都需要在上面执行完
                        // 如果什么消息都没有，就调用青云客
                        if (qqMessage.getReplyMessages().isEmpty()){
                            if (!qqMessage.getContent().contains("http")){
                                qqMessage.putReplyMessage(QingYunKeApi.getMessage(qqMessage.getContent()));
                            }
                        }
                    }
                } else {
                    execute(qqMessage);
                }
            }
            execute(qqMessage);
        }

        sendMessage(qqMessage);
    }

    private void executeSim(QqMessage qqMessage) {
        String command = subCommand(qqMessage.getContent());
        getSimPlugins(qqMessage).forEach((key, atCommand) -> {
            if (command.equals(key)) {
                atCommand.execute(qqMessage);
            }
        });
    }

    private void executeAt(QqMessage qqMessage) {
        String command = subCommand(qqMessage.getContent());
        getAtPlugins(qqMessage).forEach((key, atCommand) -> {
            if (command.equals(key)) {
                atCommand.execute(qqMessage);
            }
        });
    }

    @PostConstruct
    public void init(){
        log.info("开始初始化所有#指令！");
        // 初始化插件
        SpringUtil.getBeansOfType(RobotPlugin.class).entrySet().stream().forEach(
          entity -> {
              log.info("{}====>{}", entity.getKey(), entity.getValue().getClass().getName());
              QqPlugin old = qqPluginService.query().eq("class_name", entity.getValue().getClass().getName()).one();
              if(old == null){
                  log.info("自动添加插件到数据库：{}====>{}", entity.getValue().getName(), entity.getValue().getClass().getName());
                  QqPlugin qqPlugin = new QqPlugin();
                  qqPlugin.setName(entity.getValue().getName());
                  qqPlugin.setPluginDesc(entity.getValue().getDesc());
                  qqPlugin.setClassName(entity.getValue().getClass().getName());
                  qqPlugin.setSort(entity.getValue().getSort());
                  qqPlugin.setIsAll(0);
                  qqPlugin.setState(1);
                  qqPluginService.save(qqPlugin);
              }
          }
        );

        allPlugins = qqPluginService.query().orderByAsc("sort").list();
        allPlugins = allPlugins.stream().filter(qqPlugin -> {
            try {
                Class<RobotPlugin> clazz = (Class<RobotPlugin>) Class.forName(qqPlugin.getClassName());
                return true;
            } catch (ClassNotFoundException e) {
                log.error("未找到插件："+e.getMessage());
                return false;
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return false;
            }
        }).collect(Collectors.toList());

        allPluginVos = allPlugins.stream().map(item -> getQqPluginVo(item, null)).filter(item -> item != null).collect(Collectors.toList());
        publickPlugins = initPlugin();
        qqPluginIdMap.clear();
        qqPluginNameMap.clear();
        manages.clear();
        allPlugins.forEach(item -> {
            qqPluginIdMap.put(item.getId(), item);
            qqPluginNameMap.put(item.getName(), item);
        });
        List<QqGroupPlugin> groupPlugins = qqGroupPluginService.query().list();
        groupPlugins.forEach(item -> {
            QqPlugin qqPlugin = qqPluginIdMap.get(item.getPluginId());
            if(qqPlugin != null) {
                List<QqPluginVo> groupPluginList = manages.get(item.getGroupId());
                if (groupPluginList == null) {
                    groupPluginList = new ArrayList<>();
                    manages.put(item.getGroupId(), groupPluginList);
                }
                QqPluginVo qqPluginVo = getQqPluginVo(qqPlugin, item);
                if(qqPluginVo != null){
                    groupPluginList.add(qqPluginVo);
                }
            }
        });
        manages.entrySet().forEach((entry) -> {
            entry.setValue(robotPluginSort(entry.getValue()));
        });

        log.info("开始初始化所有@命令！");
        SpringUtil.getBeansOfType(AtPlugin.class).forEach((key, value) -> {
            log.info("{}====>{}", key, value.getClass().getName());
            try {
                AtPlugin atPlugin = (AtPlugin) SpringUtil.getBean(Class.forName(value.getClass().getName()));
                atNamePluginMap.put(atPlugin.getName(), atPlugin);
                Map<String, AtCommand> commands = atPlugin.getCommands();
                if (!commands.isEmpty()) {
                    atCommondPluginMap.putAll(commands);
                    for (Map.Entry<String, AtCommand> entry : commands.entrySet()) {
                        atPluginDescMap.put(entry.getKey(), entry.getValue().getDesc());
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("开始初始化所有普通命令！");
        SpringUtil.getBeansOfType(BaseAbstractSim.class).forEach((key, value) -> {
            log.info("{}====>{}", key, value.getClass().getName());
            try {
                BaseAbstractSim baseAbstractSim = (BaseAbstractSim) SpringUtil.getBean(Class.forName(value.getClass().getName()));
                String name = baseAbstractSim.getName();
                Map<String, SimCommand> commands = baseAbstractSim.getCommands();
                if (!commands.isEmpty()){
                    simCommandMap.putAll(commands);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        // 初始化分类
        if (!simCommandMap.isEmpty()) {
            baseSimMap = simCommandMap.values().stream().collect(Collectors.groupingBy(SimCommand::getClassify));
        }
    }

    private void execute(QqMessage qqMessage){
        getPlugins(qqMessage).forEach(item -> {
            if (qqMessage.isExecuteNext() && item.isEnabled() && item.getRobotPlugin().isEnabled()) {
                item.getRobotPlugin().execute(qqMessage);
            }
        });
    }

    /**
     * 获取所有插件的指令列表
     * @param qqMessage
     * @return
     */
    private String getCommandsStr(QqMessage qqMessage){
        StringBuilder strMsg = new StringBuilder("我是" + qqConfig.getName() + "！~~~\n指令列表：\n");
        int n = 0;
        if(qqMessage.isManager()){
            n++;
            strMsg.append(n+ "、管理    所有的管理命令\n");
        }
        Set<String> allCommands = new TreeSet<>();
        getPlugins(qqMessage).forEach(item -> {
            if(item.isEnabled() && item.getRobotPlugin().isEnabled()){
                if(qqConfig.isSimplifyCommand()){
                    allCommands.addAll(item.getRobotPlugin().getAllMasterCommands());
                }else {
                    allCommands.addAll(item.getRobotPlugin().getAllCommands(qqMessage));
                }
            }
        });
        for (String command : allCommands) {
            n++;
            strMsg.append(n+ "、"+command + '\n');
        }

        return strMsg.toString();
    }
    /**
     * 转换qq群插件
     * @param qqPlugin
     * @param qqGroupPlugin
     * @return
     */
    private QqPluginVo getQqPluginVo(QqPlugin qqPlugin, QqGroupPlugin qqGroupPlugin){
        if(qqPlugin != null) {
            QqPluginVo qqPluginVo = new QqPluginVo();
            qqPluginVo.setPluginid(qqPlugin.getId());
            if(qqGroupPlugin != null){
                qqPluginVo.setId(qqGroupPlugin.getId());
                qqPluginVo.setSort(qqGroupPlugin.getSort());
                qqPluginVo.setPluginEnabled(qqGroupPlugin.getEnabled());
            }else{
                qqPluginVo.setSort(qqPlugin.getSort());
                qqPluginVo.setPluginEnabled(qqPlugin.getState());
            }

            try {
                Class<RobotPlugin> clazz = (Class<RobotPlugin>) Class.forName(qqPlugin.getClassName());
                RobotPlugin robotPlugin = SpringUtil.getBean(clazz);
                robotPlugin.setName(qqPlugin.getName());
                qqPluginVo.setRobotPlugin(robotPlugin);
            } catch (ClassNotFoundException e) {
                log.error("未找到插件："+e.getMessage());
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return null;
            }
            return qqPluginVo;
        }else{
            return null;
        }
    }

    private Set<Long> getRootManageQq() {
        return qqConfig.getRootManageQq();
    }

    private Set<Long> getGroupWhiteList() {
        return manages.keySet();
    }

    /**
     * 添加机器人状态消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addRobotStatusMessage(QqMessage qqMessage) {
        qqMessage.putReplyMessage(String.format("机器人状态：%s", enabled));
    }

    /**
     * 添加管理员列表信息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addManageMessage(QqMessage qqMessage) {
        StringBuilder message = new StringBuilder();
        message.append("root管理员列表：\n");
        getRootManageQq().forEach(item -> {
            message.append(item).append("\n");
        });
        message.append("普通管理员列表：\n");
        NORMAL_MANAGE_QQ.forEach(item -> {
            message.append(item).append("\n");
        });
        qqMessage.putReplyMessage(message.toString());
    }
    /**
     * 添加管理员列表
     *
     * @param qqMessage 机器人插件上下文
     * @param qqStr   qq号
     * @param add     是否添加
     */
    private void addManage(QqMessage qqMessage, String qqStr, boolean add) {
        Long qq = getQq(qqStr);
        if (qq != null) {
            if (add) {
                NORMAL_MANAGE_QQ.add(qq);
            } else {
                NORMAL_MANAGE_QQ.remove(qq);
            }

            this.addManageMessage(qqMessage);
        }else{
            String error = StrUtil.format("转换qq失败[{}]", qq);
            qqMessage.putReplyMessage(error);
            return;
        }
    }

    /**
     * 添加群白名单列表
     *
     * @param qqMessage 机器人插件上下文
     * @param qqStr      添加的群号
     * @param add     是否添加
     */
    private void addGroupList(QqMessage qqMessage, String qqStr, boolean add) {
        Long groupId = getQq(qqStr);
        if (groupId != null) {
            if (add) {
                if(!manages.containsKey(groupId)){
                    manages.put(groupId, initPlugin());
                }
            } else {
                manages.remove(groupId);
                qqGroupPluginService.remove(new QueryWrapper<QqGroupPlugin>().eq("group_id", groupId));
            }
            this.addGroupListMessage(qqMessage);
        }else{
            String error = StrUtil.format("转换qq群号失败[{}]", groupId);
            qqMessage.putReplyMessage(error);
            return;
        }
    }

    /**
     * 添加群插件列表
     *
     * @param qqMessage 机器人插件上下文
     * @param pluginName  插件名称
     * @param add     是否添加
     */
    private void addPluginsList(QqMessage qqMessage, String pluginName, boolean add) {
        String[] pluginNames = pluginName.split("\\s+");
        Long groupId = qqMessage.getGroupId();
        QqPluginVo qqPluginVo = null;
        if(pluginNames.length == 1){
            if(groupId != null) {
                qqPluginVo = getQqPluginVo(qqPluginNameMap.get(pluginNames[0]), null);
            }
        }else  if(pluginNames.length == 2){
            groupId = getQq(pluginNames[1]);
            qqPluginVo = getQqPluginVo(qqPluginNameMap.get(pluginNames[0]), null);
        }
        if (qqPluginVo != null && groupId != null) {
            Group group = MessageUtil.getGroup(groupId);

            if (group != null) {
                QqGroupPlugin old = qqGroupPluginService.query().eq("plugin_id", qqPluginVo.getPluginid()).eq("group_id", groupId).one();
                List<QqPluginVo> groupPluginList = manages.get(groupId);
                if (add) {
                    if(groupPluginList == null){
                        groupPluginList = new ArrayList<>();
                    }
                    if(old == null){
                        groupPluginList.add(qqPluginVo);
                        QqGroupPlugin qqGroupPlugin = new QqGroupPlugin();
                        qqGroupPlugin.setPluginId(qqPluginVo.getPluginid());
                        qqGroupPlugin.setGroupId(groupId);
                        qqGroupPlugin.setEnabled(1);
                        qqGroupPlugin.setSort(qqPluginVo.getSort());
                        qqGroupPlugin.setCreateId(qqMessage.getSender().getId());
                        qqGroupPlugin.setCreateBy(qqMessage.getSender().getNick());
                        qqGroupPlugin.setCreateTime(new Date());
                        qqGroupPluginService.save(qqGroupPlugin);
                        qqPluginVo.setId(qqGroupPlugin.getId());
                    }

                    manages.put(groupId, robotPluginSort(groupPluginList));
                } else {
                    if(groupPluginList != null){
                        groupPluginList.remove(qqPluginVo);
                        if(old != null){
                            qqGroupPluginService.removeById(old.getId());
                        }
                    }
                }
                //返回插件列表
                this.addPluginsListMessage(qqMessage, groupId);

                if(add){
                    qqPluginVo.getRobotPlugin().init(groupId);
                }else{
                    qqPluginVo.getRobotPlugin().cancel(groupId);
                }
            }else{
                String error = StrUtil.format("未找到QQ群：[{}]", groupId);
                qqMessage.putReplyMessage(error);
            }
        }else if(groupId == null){
            String error = StrUtil.format("未找到qq群");
            qqMessage.putReplyMessage(error);
        }else if(qqPluginVo == null){
            String error = StrUtil.format("未找到[{}]", pluginNames[0]);
            qqMessage.putReplyMessage(error);
        }
    }

    /**
     * 转换qq
     * @param qqStr
     * @return
     */
    private Long getQq(String qqStr){
        Long qq = null;
        if(!StringUtil.isBlank(qqStr)){
            try {
                qq = Long.parseLong(qqStr);
            }catch (Exception e){
                log.error("转换qq号异常：{}", qqStr);
            }
        }
        return qq;
    }

    /**
     * 获取插件列表
     *
     * @param qqMessage qq消息
     */
    private List<QqPluginVo> getPlugins(QqMessage qqMessage) {
        if(qqMessage.getGroupId() != null){
            return manages.getOrDefault(qqMessage.getGroupId(), publickPlugins);
        } else if(qqMessage.isManager()){
            return allPluginVos;
        } else {
            return publickPlugins;
        }
    }

    /**
     * 获取艾特插件列表
     *
     * @param qqMessage qq消息
     */
    private Map<String, AtCommand> getAtPlugins(QqMessage qqMessage) {
        return atCommondPluginMap;
    }

    /**
     * 获取艾特插件列表
     *
     * @param qqMessage qq消息
     */
    private Map<String, SimCommand> getSimPlugins(QqMessage qqMessage) {
        return simCommandMap;
    }

    /**
     * 获取群插件列表
     *
     * @param groupId qq群Id
     */
    private List<QqPluginVo> getPlugins(Long groupId) {
        return manages.getOrDefault(groupId, publickPlugins);
    }

    /**
     * 添加群白名单列表消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addGroupListMessage(QqMessage qqMessage) {
        StringBuffer text = new StringBuffer("群白名单：");
        getGroupWhiteList().forEach(item -> {
            text.append("\n").append(item);
        });
        qqMessage.putReplyMessage(text.toString());
    }

    /**
     * 添加插件列表消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addAllPluginsListMessage(QqMessage qqMessage) {
        StringBuffer text = new StringBuffer();
        if(qqMessage.isManager()){
            allPlugins.forEach(item -> {
                text.append(String.format(template, item.getName(), item.getState()>0));
            });
        }
        if(!text.toString().equals("")){
            qqMessage.putReplyMessage(text.toString());
        }
    }

    /**
     * 添加插件列表消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addPluginsListMessage(QqMessage qqMessage) {
        addPluginsListMessage(qqMessage, qqMessage.getGroupId());
    }

    /**
     * 添加插件列表消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addPluginsListMessage(QqMessage qqMessage, String groupIdStr) {
        Long groupId = getQq(groupIdStr);

        if(groupId != null) {
            addPluginsListMessage(qqMessage, groupId);
        }
    }

    /**
     * 添加插件列表消息
     *
     * @param qqMessage 机器人插件上下文
     */
    private void addPluginsListMessage(QqMessage qqMessage, Long groupId) {
        if(groupId != null) {
            StringBuffer text = new StringBuffer();

            getPlugins(groupId).forEach((item) -> {
                text.append(String.format(template, item.getRobotPlugin().getName(), item.isEnabled()));
            });

            if (!text.toString().equals("")) {
                qqMessage.putReplyMessage(text.toString());
            }
        }
    }


    /**
     * 初始化插件内容
     */
    public List<QqPluginVo> initPlugin() {
        List<QqPluginVo> qqPluginVos = allPlugins.stream().filter(item -> item.getIsAll()== 1).map(item -> getQqPluginVo(item, null)).filter(item -> item != null ).collect(Collectors.toList());
        return qqPluginVos;
    }

    /**
     * 根据插件名称启动插件
     *
     * @param qqMessage 机器人插件上下文
     * @param pluginName    插件名称{@link RobotPlugin#info()}
     * @param start   是否启用
     */
    public void startPlugin(QqMessage qqMessage, String pluginName, boolean start) {
        String[] pluginNames = pluginName.split("\\s+");
        String name = pluginNames[0];
        Long groupId = null;
        QqPluginVo qqPluginVo = null;
        if(pluginNames.length == 1){
            if(groupId != null) {
                groupId = qqMessage.getGroupId();
            }
        }else  if(pluginNames.length == 2){
            groupId = getQq(pluginNames[1]);
        }
        if(groupId != null) {
            getPlugins(groupId).forEach(item -> {
                if (item.getRobotPlugin().getName().equals(name)) {
                    item.setEnabled(start);
                    if (item.getId() == null) {
                        QqGroupPlugin qqGroupPlugin = new QqGroupPlugin();
                        qqGroupPlugin.setGroupId(qqMessage.getGroupId());
                        qqGroupPlugin.setPluginId(item.getPluginid());
                        qqGroupPlugin.setSort(item.getSort());
                        qqGroupPlugin.setEnabled(start ? 1 : 0);
                        qqGroupPlugin.setCreateId(qqMessage.getSender().getId());
                        qqGroupPlugin.setCreateBy(qqMessage.getSender().getNick());
                        qqGroupPlugin.setCreateTime(new Date());
                        qqGroupPluginService.save(qqGroupPlugin);
                        item.setId(qqGroupPlugin.getId());
                    } else {
                        qqGroupPluginService.update().set("enabled", start ? 1 : 0).eq("id", item.getId()).update();
                    }
                    if (start) {
                        item.getRobotPlugin().init(qqMessage.getGroupId());
                    } else {
                        item.getRobotPlugin().cancel(qqMessage.getGroupId());
                    }
                }
            });
            this.addPluginsListMessage(qqMessage, groupId);
        }
    }

    public List<QqPluginVo>  robotPluginSort(List<QqPluginVo> plugins) {
        plugins.addAll(initPlugin());
        return plugins.stream().distinct().sorted().collect(Collectors.toList());
    }

    /**
     * 从上下文中获取需要发送的消息
     *
     * @param qqMessage 机器人上下文
     */
    private void sendMessage(QqMessage qqMessage) {
        if (!qqMessage.getReplyMessages().isEmpty()) {
            qqMessage.getReplyMessages().entries().forEach(entry -> {
                entry.getKey().sendMessage(entry.getValue());
            });
        }
    }

    /**
     * 根据关键字返回音乐
     */
    private void sendMusic(QqMessage qqMessage){
        String content = qqMessage.getContent();
        // 截取后面的关键字
        String keyword = subParam(content);
        MusicSource musicSource = MusicFactory.getMusicSource(subCommand(content));
        if (musicSource == null)
            throw new IllegalArgumentException("music source not exists");
        // 此处使用默认样板
        MusicCardProvider cb = MusicFactory.getCard("Mirai");
        if (cb == null)
            throw new IllegalArgumentException("card template not exists");

        MusicInfo musicInfo;
        try {
            musicInfo = musicSource.get(keyword);
        } catch (Throwable t) {
            qqMessage.putReplyMessage("无法找到歌曲" + keyword);
            return;
        }
        try {
            Message m = cb.process(musicInfo, qqMessage.getContact());
            if (m != null) {
                qqMessage.putReplyMessage(m);
                return;
            }
        } catch (Throwable t) {
            log.error("封装音乐消息失败！");
        }
        qqMessage.putReplyMessage("分享歌曲失败。");
    }

    /**
     * 从指令中截取命令
     * @param content 指令
     * @return 命令
     */
    @NotNull
    public static String subCommand(String content) {
        int end = content.indexOf(split);
        if (end < 0){
            end = content.length();
        }
        return content.substring(0, end);
    }

    /**
     * 从指令中截取参数
     * @param content 指令
     * @return 参数
     */
    @NotNull
    public static String subParam(String content) {
        int start = content.indexOf(split);
        if (start < 0){
            return "";
        }else {
            return content.substring(start + 1);
        }
    }

    /**
     * 根据词库进行聊天
     */
    private void chatByLexicon(QqMessage qqMessage){
        AbstractDict chatPattern = ChatPlugin.getChatPattern();
        chatPattern.chat(qqMessage);
    }

    /**
     * 获取@指令集
     */
    public Map<String, String> getAtPluginDescMap() {
        return atPluginDescMap;
    }

    /**
     * 获取指定分类的菜单
     */
    public List< SimCommand> getSimPluginDescMap(String name) {
        return baseSimMap.get(name);
    }

}
