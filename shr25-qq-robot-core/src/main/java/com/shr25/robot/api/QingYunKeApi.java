package com.shr25.robot.api;

import com.google.common.collect.Multimap;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.IOUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 青云客 API 类，官网 <a href="http://api.qingyunke.com/">http://api.qingyunke.com/</a>
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public class QingYunKeApi implements AbstractApiMessage{
    public static final String url = "http://api.qingyunke.com/api.php";

    public static String getMessage(String queryContent) {
        try {
            URL obj = new URL(url + "?key=free&appid=0&msg=" + URLEncoder.encode(queryContent, "UTF-8"));
            Map map = IOUtil.sendAndGetResponseMap(obj, "GET", null, null);
            return ((String) map.get("content")).replaceAll("\\{br\\}", "\n");
        } catch (Exception e) {
            e.printStackTrace();
            return "理解不了呢";
        }
    }

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        return null;
    }
}
