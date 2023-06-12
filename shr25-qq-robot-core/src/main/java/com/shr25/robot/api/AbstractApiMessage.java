package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.base.BaseAbstractSim;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.util.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.net.URL;

public abstract interface AbstractApiMessage extends BaseAbstractSim {

    String name = "API";

    default String getName(){
        return name;
    }

    /**
     * 将要回复的消息，子类需要实现
     */
    public abstract Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage);

    default void sendImage(QqMessage qqMessage, String imgUrl) {
        if (imgUrl == null){
            qqMessage.putReplyMessage("~~~~~~");
            return;
        }

        try {
            qqMessage.putReplyMessage(MessageUtil.createBuilder().append(qqMessage.uploadImage(new URL(imgUrl))).build());
        }catch (Exception e){
            log.info(String.format("Not image url: %s", imgUrl));
        }
    }

}
