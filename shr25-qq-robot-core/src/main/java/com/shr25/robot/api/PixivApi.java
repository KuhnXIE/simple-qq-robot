package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.exception.CanNotSendMessageException;
import com.shr25.robot.exception.FileUploadException;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.RobotManagerService;
import com.shr25.robot.qq.util.MessageUtil;
import com.shr25.robot.utils.IOUtil;
import com.shr25.robot.utils.MapGetter;
import com.shr25.robot.utils.StringUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
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
@Service
public class PixivApi implements AbstractApiMessage{

    public final String mysteriousImage = "神秘代码";
    public final String seImage = "涩图";
    public final String seImagePlus = "高清涩图";
    public final String beautifulImage = "美图";
    @Autowired
    private QqConfig qqConfig;

    public PixivApi() {
        super();
        log.info("开始加载 pixiv 插件~");

        addCommand("神秘代码", "获取神秘代码，未知~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });

        addCommand("美图", "获取一张美图~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });

        addCommand("涩图", "获取一张涩图~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });

        addCommand("高清涩图", "获取一张高清涩图~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });
    }


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
        String content = qqMessage.getContent();
        String command = RobotManagerService.subCommand(content);
        // 神秘代码
        if (command.equals(mysteriousImage)) {
            // 需要分割 tag
            List<String> tags = getTags(content, mysteriousImage);
            doParseMysteriousImage(qqMessage, tags);
        }else if (command.equals(seImage)) {
            doParseSeImageAndSend(qqMessage, getTags(content, command), false);
        }else if (command.equals(seImagePlus)) {
            doParseSeImageAndSend(qqMessage, getTags(content, command), true);
        }else if (command.equals(beautifulImage)) {
            doParseBeautifulImage(qqMessage);
        }

        return qqMessage.getReplyMessages();
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

    /**
     * 获取神秘代码
     */
    private void doParseMysteriousImage(QqMessage qqMessage, List<String> tags){
        String imgUrl = null;
        try {
            imgUrl = PixivApi.searchImage(tags, false, false);
        } catch (IOException e) {
            qqMessage.putReplyMessage("查无此图！");
        }

        sendImage(qqMessage, imgUrl);
    }

    /**
     * 获取涩图
     *
     * @param isPlus 是否是高清涩图
     */
    private void doParseSeImageAndSend(QqMessage qqMessage, List<String> tags, boolean isPlus) {
        if (!qqConfig.isColorSwitch()) {
            return;
        }
        String imgUrl = null;
        try {
            imgUrl = PixivApi.searchImage(tags, true, !isPlus);
        } catch (IOException e) {
            qqMessage.putReplyMessage("查无此图！");
        }

        sendImage(qqMessage, imgUrl);
    }

    /**
     * 获取美图
     */
    private void doParseBeautifulImage(QqMessage qqMessage) {
        sendImage(qqMessage, beautifulImage);
    }


}

