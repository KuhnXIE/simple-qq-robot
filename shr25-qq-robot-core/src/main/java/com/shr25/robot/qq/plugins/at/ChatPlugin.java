package com.shr25.robot.qq.plugins.at;

import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.model.dict.AbstractDict;
import com.shr25.robot.qq.model.dict.CuteDic;
import com.shr25.robot.qq.model.dict.TsundereDic;
import com.shr25.robot.qq.service.RobotManagerService;
import com.shr25.robot.qq.service.dict.CuteDicService;
import com.shr25.robot.qq.service.dict.TsundereDicService;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天工具
 */
@Service
public class ChatPlugin extends AtPlugin{

    public ChatPlugin() {
        super();
        log.info("开始加载 聊天 插件~");
        setName("聊天插件");
        addDesc("设置聊天参数信息！");

        addCommand("修改聊天模式", "对机器人的词库进行修改~", RobotMsgPermission.ADMIN, qqMessage -> {
            setChatPattern(qqMessage);
            return true;
        });

        addCommand("聊天学习", "让机器人学习新的回复~", RobotMsgPermission.ADMIN, qqMessage -> {
            study(qqMessage);
            return true;
        });
    }

    private static Map<String, AbstractDict> dictMap = new HashMap<>();

    @Autowired
    private CuteDic cuteDic;
    @Autowired
    private TsundereDic tsundereDic;

    /**
     * 初始化词典
     */
    private static final Logger logger = LoggerFactory.getLogger(ChatPlugin.class);

    /**
     * 聊天使用的模式
     */
    private static AbstractDict chatPattern;

    @Resource
    private CuteDicService cuteDicService;
    @Resource
    private TsundereDicService tsundereDicService;

    @PostConstruct
    public void initDict(){
        logger.info("开始初始化词典！");

        List<CuteDic> cuteDics = cuteDicService.list();
        List<TsundereDic> tsundereDics = tsundereDicService.list();

        if (!cuteDics.isEmpty()){
            CuteDic.cuteMap = cuteDics.stream().filter(e -> e.getKeyWord() != null).collect(Collectors.groupingBy(CuteDic::getKeyWord));
        }
        if (!tsundereDics.isEmpty()){
            TsundereDic.tsundereDicMap = tsundereDics.stream().filter(e -> e.getKeyWord() != null).collect(Collectors.groupingBy(TsundereDic::getKeyWord));
        }
        logger.info("初始化词典完成！");

        // 初始化词典
        dictMap.put(cuteDic.type(), cuteDic);
        dictMap.put(tsundereDic.type(), tsundereDic);
        chatPattern = dictMap.get("可爱");
    }

    public static AbstractDict getChatPattern() {
        return chatPattern;
    }

    /**
     * 修改回复模式
     */
    public void setChatPattern(QqMessage qqMessage) {
        String content = qqMessage.getContent();
        // 获取参数
        String param = RobotManagerService.subParam(content);
        AbstractDict abstractDict = dictMap.get(param);
        // 构建链式消息
        At at = new At(qqMessage.getGroup().getOwner().getId());
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageChainBuilder.append(at);
        if (abstractDict == null){
            messageChainBuilder.append("不存在该模式！");
        }else {
            ChatPlugin.chatPattern = abstractDict;
            messageChainBuilder.add("设置成功，当前为" + abstractDict.type() + "模式！");
        }
        qqMessage.putReplyMessage(messageChainBuilder.build());
    }

    public void study(QqMessage qqMessage) {
        String content = qqMessage.getContent();
        // 获取参数
        String param = RobotManagerService.subParam(content);
        String[] studyKey = param.split(RobotManagerService.split);
        // 构建链式消息
        At at = new At(qqMessage.getGroup().getOwner().getId());
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageChainBuilder.append(at);

        if (studyKey.length != 2){
            messageChainBuilder.append("学习失败！ 实例:[聊天学习 在吗 我在的~]");
        }else {
            getChatPattern().add(studyKey[0], studyKey[1]);
            messageChainBuilder.append("学习成功~");
        }
        qqMessage.putReplyMessage(messageChainBuilder.build());
    }

}
