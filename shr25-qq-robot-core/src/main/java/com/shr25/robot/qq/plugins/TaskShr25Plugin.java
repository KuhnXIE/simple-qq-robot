package com.shr25.robot.qq.plugins;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.util.MessageUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 树人助人分享信息
 *
 * @author huobing
 * @date 2022/6/8 16:44
 */
@Component
public class TaskShr25Plugin extends RobotPlugin {
    public TaskShr25Plugin() {
        super();
        setName("树人分享插件");
        addDesc("查找树人分享的信息");
        addCommand("#树人分享","查看所有树人分享命令", true);
        addCommand("#玩游戏","边玩游戏边赚钱");
        addCommand("#调研","网络调研平台");
        setSort(1);
    }

    public boolean executeMessage(QqMessage qqMessage) {
        AtomicBoolean executeNext = new AtomicBoolean(true);
        // 消息为空、好友消息、群里被@
        if (!StringUtil.isBlank(qqMessage.getContent())) {
            //排除机器人号段
            if (!qqMessage.isSysBot()) {
                // 记录机器人是否被艾特
                AtomicBoolean flag = new AtomicBoolean(false);
                // 接收的字符串消息
                StringBuilder strMsg = new StringBuilder();
                if (qqMessage.getEvent() instanceof GroupMessageEvent) {
                    qqMessage.getGroupMessageEvent().getMessage().forEach(singleMessage -> {
                        // 过滤艾特消息
                        if (singleMessage instanceof At) {
                            if (((At) singleMessage).getTarget() == qqMessage.getMessageEvent().getBot().getId()) {
                                flag.set(true);
                            }
                        } else {
                            strMsg.append(singleMessage.contentToString().trim());
                        }
                    });
                }
                // 如果是群消息，则需要被@才回复消息
                if (flag.get()) {
                    String retMsg = null;
                    if (strMsg.length() > 0) {
                        retMsg = shr25(qqMessage.getGroupId(), strMsg.toString());
                    }else{
                        retMsg = shr25(qqMessage.getGroupId(), "官网");
                    }

                    if (retMsg != null && !retMsg.trim().equals("")) {
                        MessageChainBuilder builder = MessageUtil.createBuilder();
                        builder.append(new At(qqMessage.getSenderId()));
                        builder.append(new PlainText(retMsg));
                        qqMessage.putReplyMessage(builder);
                        executeNext.set(false);
                    }
                }
            }
        }
        return executeNext.get();
    }

    public String shr25(Long groupId, String msg) {
        String url = "https://task.shr25.com/robot/get";
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("groupId", groupId);
            data.put("msg", msg);
            return JSONUtil.parseObj(HttpUtil.post(url, data)).getStr("data");
        } catch (Exception e) {
            log.error("请求树人分享系统失败！", e);
            return "";
        }
    }
}
