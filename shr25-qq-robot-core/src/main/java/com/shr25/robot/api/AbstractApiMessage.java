package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.base.BaseAbstractSim;
import com.shr25.robot.qq.model.QqMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

public abstract interface AbstractApiMessage extends BaseAbstractSim {

    String name = "API";

    default String getName(){
        return name;
    }

    /**
     * 将要回复的消息，子类需要实现
     */
    public abstract Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage);

}
