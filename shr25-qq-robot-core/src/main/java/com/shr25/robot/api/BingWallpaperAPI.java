package com.shr25.robot.api;


import com.google.common.collect.Multimap;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.IOUtil;
import com.shr25.robot.utils.MapGetter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.net.URL;

/**
 * Bing 壁纸 API
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
public class BingWallpaperAPI implements AbstractApiMessage{
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
        return null;
    }
}
