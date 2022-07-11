package com.shr25.robot.qq.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.model.Vo.QqPluginVo;
import com.shr25.robot.qq.model.QqGroupPlugin;
import com.shr25.robot.qq.model.qqPlugin.QqPlugin;
import com.shr25.robot.qq.plugins.RobotPlugin;
import com.shr25.robot.qq.service.qqGroup.IQqGroupPluginService;
import com.shr25.robot.qq.service.qqPlugin.IQqPluginService;
import com.shr25.robot.qq.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
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

    public String getDesc() {
        return "系统管理 使用方式：\n"
          + "#初始化\n"
          + "#开机\n"
          + "#关机\n"
          + "#管理员列表\n"
          + "#添加管理员 {qq号/@群成员}\n"
          + "#删除管理员 {qq号/@群成员}\n"
          + "#群白名单\n"
          + "#添加群 {群号}\n"
          + "#删除群 {群号}\n"
          + "#机器人状态\n"
          + "#全部插件/#所有插件\n"
          + "#插件列表 {群号，管理员发群消息可以省略}\n"
          + "#添加插件 {插件名称} {群号，管理员发群消息可以省略} \n"
          + "#删除插件 {插件名称} {群号，管理员发群消息可以省略} \n"
          + "#开启插件 {插件名称}\n"
          + "#关闭插件 {插件名称}\n"
          + "#插件详情 {插件名称}\n";
    }

    public void publishMessage(Event event) {
        QqMessage qqMessage = new QqMessage(event, qqConfig, NORMAL_MANAGE_QQ);

        if(qqMessage.getMessageType() != 0){
            if(!StringUtils.isBlank(qqMessage.getContent())) {
                switch (qqMessage.getContent()) {
                    case "help":
                    case "简介":
                    case "帮助":
                        qqMessage.putReplyMessage(getCommandsStr(qqMessage));
                        break;
                    case "#管理":
                        if (qqMessage.isManager()) {
                            qqMessage.putReplyMessage(getDesc());
                        }
                        break;
                    case "#初始化":
                        if (qqMessage.isManager()) {
                            init();
                            qqMessage.putReplyMessage("初始化成功！~~~");
                        }
                        break;
                    case "#开机":
                        if (qqMessage.isManager()) {
                            enabled = true;
                            this.addRobotStatusMessage(qqMessage);
                        }
                        break;
                    case "#关机":
                        if (qqMessage.isManager()) {
                            enabled = false;
                            this.addRobotStatusMessage(qqMessage);
                        }
                        break;
                    case "#管理员列表":
                        if (qqMessage.isRoot()) {
                            this.addGroupListMessage(qqMessage);
                        }
                        break;
                    case "#全部插件":
                    case "#所有插件":
                        if(qqMessage.isManager()){
                            this.addAllPluginsListMessage(qqMessage);
                        }
                        break;
                    case "#插件列表":
                        if (qqMessage.isGroupMessage() && qqMessage.isCanOperatorGroup()) {
                            this.addPluginsListMessage(qqMessage);
                        }
                        break;
                    case "#机器人状态":
                        this.addRobotStatusMessage(qqMessage);
                        break;
                    default:
                        // root管理员才有的功能
                        if (qqMessage.getContent().startsWith("#添加管理员")) {
                            if (qqMessage.isRoot()) {
                                this.addManage(qqMessage, qqMessage.getParameter("#添加管理员"), true);
                            }
                        } else if (qqMessage.getContent().startsWith("#删除管理员")) {
                            if (qqMessage.isRoot()) {
                                this.addManage(qqMessage, qqMessage.getParameter("#删除管理员"), false);
                            }
                        } else if (qqMessage.getContent().startsWith("#添加群")) {
                            if (qqMessage.isManager()) {
                                this.addGroupList(qqMessage, qqMessage.getParameter("#添加群"), true);
                            }
                        } else if (qqMessage.getContent().startsWith("#删除群")) {
                            if (qqMessage.isManager()) {
                                this.addGroupList(qqMessage, qqMessage.getParameter("#删除群"), false);
                            }
                        } else if (qqMessage.getContent().startsWith("#插件列表")) {
                            if (qqMessage.isManager()) {
                                this.addPluginsListMessage(qqMessage, qqMessage.getParameter("#插件列表"));
                            }
                        }  else if (qqMessage.getContent().startsWith("#添加插件")) {
                            if (qqMessage.isManager()) {
                                this.addPluginsList(qqMessage, qqMessage.getParameter("#添加插件"), true);
                            }
                        } else if (qqMessage.getContent().startsWith("#删除插件")) {
                            if (qqMessage.isManager()) {
                                this.addPluginsList(qqMessage, qqMessage.getParameter("#删除插件"), false);
                            }
                        }  else if (qqMessage.getContent().startsWith("#开启插件")) {
                            if (qqMessage.isCanOperatorGroup() || qqMessage.isManager()) {
                                this.startPlugin(qqMessage, qqMessage.getParameter("#开启插件"), true);

                            }
                        } else if (qqMessage.getContent().startsWith("#关闭插件")) {
                            if (qqMessage.isCanOperatorGroup() || qqMessage.isManager()) {
                                this.startPlugin(qqMessage, qqMessage.getParameter("#关闭插件"), false);
                            }
                        } else {
                            //是否插件简介
                            boolean isPluginDesc = false;
                            if (qqMessage.getContent().matches("#(\\S+)$")) {
                                QqPlugin qqPlugin = qqPluginNameMap.get(qqMessage.getContent().substring(1));
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
                        }
                        break;
                }
            }else{
                if(qqMessage.getAt()) {
                    qqMessage.putReplyMessage(getCommandsStr(qqMessage));
                }
            }
        }else{
            execute(qqMessage);
        }
        sendMessage(qqMessage);
    }

    @PostConstruct
    public void init(){
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
            strMsg.append(n+ "、#管理    所有的管理命令\n");
        }

        if(qqConfig.isSimplifyCommand()){
            for (QqPluginVo item : getPlugins(qqMessage)) {
                n++;
                strMsg.append(n + "、#" + item.getRobotPlugin().getName() + '\n');
            }
        }
        Set<String> allCommands = new TreeSet<>();
        getPlugins(qqMessage).forEach(item -> {
            if(item.isEnabled() && item.getRobotPlugin().isEnabled()){
                if(qqConfig.isSimplifyCommand()){
                    allCommands.addAll(item.getRobotPlugin().getMasterCommands());
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
        if(text.toString() != ""){
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
}
