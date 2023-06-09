package com.shr25.robot.music;

import com.shr25.robot.music.card.*;
import com.shr25.robot.music.impl.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音乐的工厂类
 */
public class MusicFactory {


    /** 音乐来源. */
    public static final Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());

    /** 外观来源 */
    public static final Map<String, MusicCardProvider> cards = new ConcurrentHashMap<>();

    static {
        // 注册音乐来源
        sources.put("QQ音乐", new QQMusicSource());
        sources.put("QQ音乐HQ", new QQMusicSource());
        sources.put("网易", new NetEaseMusicSource());
        sources.put("网易电台节目", new NetEaseAdvancedRadio());
        sources.put("网易电台", new NetEaseRadioSource());
        sources.put("网易HQ", new NetEaseHQMusicSource());
        sources.put("酷狗", new KugouMusicSource());
        sources.put("千千", new BaiduMusicSource());
        sources.put("Bilibili", new BiliBiliMusicSource());
        sources.put("喜马拉雅", new XimalayaSource());

        // Register music source
        sources.put("QQMusic", new QQMusicSource());
        sources.put("QQMusicHQ", new QQMusicSource());
        sources.put("NeteaseCloudMusic", new NetEaseMusicSource());

        // 注册外观
        // cards.put("LightApp", new LightAppCardProvider());
        cards.put("LightApp", new MiraiMusicCard());
        cards.put("LightAppX", new LightAppXCardProvider());
        cards.put("XML", new XMLCardProvider());
        cards.put("Silk", new SilkVoiceProvider());
        cards.put("AMR", new AmrVoiceProvider());
        cards.put("Share", new ShareCardProvider());
        cards.put("Message", new PlainMusicInfoProvider());
        cards.put("Mirai", new MiraiMusicCard());


    }

    public static MusicCardProvider getCard(String card) {
        return cards.get(card);
    }

    public static MusicSource getMusicSource(String key) {
        return sources.get(key);
    }

    public static boolean containMusicSource(String key){
        return sources.containsKey(key);
    }

}
