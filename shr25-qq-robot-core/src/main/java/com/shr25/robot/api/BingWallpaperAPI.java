package com.shr25.robot.api;


import com.google.common.collect.Multimap;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.IOUtil;
import com.shr25.robot.utils.MapGetter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import org.springframework.stereotype.Service;

import java.net.URL;

/**
 * Bing 壁纸 API
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
@Service
public class BingWallpaperAPI implements AbstractApiMessage{

    public BingWallpaperAPI() {
        super();
        log.info("开始加载 bing 插件~");

        addCommand("风景图", "进行bing风景图搜索~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });
    }

    public static String url = "https://bing.ioliu.cn/v1/rand?type=json";

    public static String getRandomImageUrl() {
        String def = "http://h1.ioliu.cn/bing/HallesWood_ZH-CN9790575479_1920x1080.jpg?imageslim";
        try {
            MapGetter mg = IOUtil.sendAndGetResponseMapGetter(new URL(url), "GET", null, null).getMapGetter("data");
            return mg.getStringOrDefault("url", def  , true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        sendImage(qqMessage, getRandomImageUrl());
        return qqMessage.getReplyMessages();
    }
}
