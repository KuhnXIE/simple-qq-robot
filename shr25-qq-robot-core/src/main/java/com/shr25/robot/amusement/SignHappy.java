package com.shr25.robot.amusement;

import com.google.common.collect.Multimap;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.happy.SignInsService;
import com.shr25.robot.qq.util.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignHappy implements AbstractHappyMessage {

    @Autowired
    private SignInsService signInsService;

    public SignHappy() {
        super();
        log.info("开始加载 签到 插件~");

        addCommand("签到", "签到功能，每天一次~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        // 构建消息链
        MessageChainBuilder builder = MessageUtil.createBuilder();
        builder.append(new At(qqMessage.getGroup().getOwner().getId()));
        // 签到
        String message = signInsService.sign(qqMessage.getSender().getNick(), qqMessage.getSenderId());

        qqMessage.putReplyMessage(builder.append(message).build());
        return qqMessage.getReplyMessages();
    }
}
