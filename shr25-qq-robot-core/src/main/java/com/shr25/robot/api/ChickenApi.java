package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.IOUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * 生成鸡汤
 */
public class ChickenApi implements AbstractApiMessage{


    /**
     * 发送鸡汤时有一定概率发送毒鸡汤，这个概率是 [0, 1] 之间的小数
     */
    public static double duChickenSoupProbability = 0.2;

    /**
     * 鸡汤与毒鸡汤 API
     */
    public static final String chickenSoupUrl = "https://api.shadiao.pro/chp/";
    public static final String duChickenSoupUrl = "https://api.shadiao.pro/du";

    public static String generate() {
        String chickenSoupUrl = ChickenApi.chickenSoupUrl;
        // 恶搞，生成毒鸡汤
        if (Math.random() < duChickenSoupProbability) {
            chickenSoupUrl = ChickenApi.duChickenSoupUrl;
        }

        Map<String, Object> map = null;
        try {
            map = IOUtil.sendAndGetResponseMap(new URL(chickenSoupUrl), "GET", null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return (String) ((Map<?, ?>) (map.get("data"))).get("text");
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        return null;
    }
}
