package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.exception.FileUploadException;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.util.MessageUtil;
import com.shr25.robot.utils.IOUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/26
 * @email happysnaker@foxmail.com
 */
public class BaiKeApi implements AbstractApiMessage{
    public static final String api = "https://baike.baidu.com/item/";

    /**
     * 百度百科 API
     *
     * @param msg
     * @return 返回一个 map，其中 map 的 key 分别是：<strong>title、desc、content, image</strong> 以分别表示标题、描述、内容、缩略图，如果发送错误，则返回 null
     */
    public static Map<String, String> search(String msg) {
        msg = msg.trim();
        Map<String, String> map = new HashMap<>();
        try {
            URL url = new URL(api + URLEncoder.encode(msg, "UTF-8"));
            String html = IOUtil.sendAndGetResponseString(url, "GET", null, null);
            if (html.contains("百度百科错误页")) {
                return null;
            }
            Document docDesc = Jsoup.parse(html);
            Elements elements = docDesc.getElementsByAttributeValue("name", "description");
            if (elements.isEmpty()) {
                return null;
            }
            map.put("content", elements.get(0).attr("content"));


            elements = docDesc.getElementsByTag("title");
            if (elements.isEmpty()) {
                return null;
            }
            String text = elements.get(0).text().replace("_百度百科", "").trim();
            int leftIndex = text.indexOf('（'), rightIndex = text.indexOf('）');
            if (leftIndex != -1 && rightIndex != -1) {
                map.put("title", text.substring(0, leftIndex).trim());
                map.put("desc", text.substring(leftIndex + 1, rightIndex));
            } else {
                map.put("title", text);
                map.put("desc", text);
            }
            elements = docDesc.getElementsByAttributeValue("name", "image");
            if (elements.isEmpty()) {
                return null;
            }
            map.put("image", elements.get(0).attr("content"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        GroupMessageEvent event = qqMessage.getGroupMessageEvent();
        MessageChainBuilder builder = MessageUtil.createBuilder();
        builder.append(new At(event.getGroup().getOwner().getId()));
        Map<String, String> map = search(qqMessage.getContent().split(" ")[1]);
        if (map == null) {
            builder.append("检索失败，换个关键词试试吧");
            qqMessage.putReplyMessage(builder.build());
        }else {
            builder.append("标题：");
            builder.append(map.get("title")).append("\n");
            builder.append("描述：");
            builder.append(map.get("desc")).append("\n");
            builder.append(map.get("content")).append("\n");
            if (map.get("image") != null) {
                try {
                    builder.append(qqMessage.uploadImage(new URL(map.get("image"))));
                } catch (FileUploadException | MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            qqMessage.putReplyMessage(builder.build());
        }
        return qqMessage.getReplyMessages();
    }
}
