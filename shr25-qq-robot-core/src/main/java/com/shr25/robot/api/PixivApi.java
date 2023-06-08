package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.IOUtil;
import com.shr25.robot.utils.MapGetter;
import com.shr25.robot.utils.StringUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * P 站 API
 * @author Happysnaker
 * @description
 * @date 2022/1/20
 * @email happysnaker@foxmail.com
 */
public class PixivApi implements AbstractApiMessage{

    private static Logger logger = LoggerFactory.getLogger(PixivApi.class);

    public static final String pidApi = "https://api.lolicon.app/setu/v2?size=original&size=small";

    // ---------------- 一些其他直接使用的 API ---------------
    /**
     * 二次元图片 API
     */
    public static final String beautifulImageUrl = "https://api.vvhan.com/api/acgimg";

    /**
     * 颜色开关
     */
    public static boolean colorSwitch = true;

    public static String searchImage(List<String> tags, boolean r18, boolean small) throws IOException {
        StringBuilder url = new StringBuilder(pidApi);
        if (r18) {
            url.append("&r18=1");
        }
        if (tags != null) {
            url.append("&");
            for (String tag : tags) {
                url.append("tag=").append(URLEncoder.encode(tag, "UTF-8")).append("&");
            }
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }
        logger.debug("pixiv search url = " + url);
        List<MapGetter> map = IOUtil.sendAndGetResponseMapGetter(new URL(url.toString()), "GET", null, null).getMapGetterList("data");
        if (map == null || map.isEmpty()) {
            return null;
        }
        return small ?
                map.get(0).getMapGetter("urls").getString("small") :
                map.get(0).getMapGetter("urls").getString("original");
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        return null;
    }

    /**
     * 根据 plantContent 获取 tag
     *
     * @param content plantContent
     * @param tem     关键词
     * @return 返回去除关键词后分割空格检索出的 tags
     */
    private List<String> getTags(String content, String tem) {
        List<String> tags = new ArrayList<>();
        List<String> strings = StringUtil.splitSpaces(content.replace(tem, ""));
        for (String s : strings) {
            if (!s.isEmpty() && !s.equals(tem)) {
                tags.add(s.trim());
            }
        }
        return tags;
    }

}

