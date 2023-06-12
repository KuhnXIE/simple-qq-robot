package com.shr25.robot.api;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Multimap;
import com.shr25.robot.common.RobotMsgPermission;
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
import org.springframework.stereotype.Service;

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
@Service
public class BaiKeApi implements AbstractApiMessage{

    private static final Map<String, String> map = new HashMap<>();

    private static String suggest = "https://baike.baidu.com/api/searchui/suggest?wd=%s&enc=utf8";

    static {
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36 Edg/83.0.478.50");
        map.put("cookie", "BDUSS=FlzTi1tUTdDbkdaazdaQ35ZUC0wWC1DQVZhVE11bGtSSnZVUEVOTktiaEF0MmxrSVFBQUFBJCQAAAAAAAAAAAEAAAAW9X831PXN~c~rzfLW2gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAqQmRAKkJkZ; BDUSS_BFESS=FlzTi1tUTdDbkdaazdaQ35ZUC0wWC1DQVZhVE11bGtSSnZVUEVOTktiaEF0MmxrSVFBQUFBJCQAAAAAAAAAAAEAAAAW9X831PXN~c~rzfLW2gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAqQmRAKkJkZ; BIDUPSID=D1024E16C764EFA4BBC62CDC6722E931; PSTM=1682321591; BAIDUID=B61CF78C84C186B5E9973DFF533BF236:FG=1; MCITY=-257%3A; BAIDUID_BFESS=B61CF78C84C186B5E9973DFF533BF236:FG=1; ZFY=QlxaloVf9oUKWxmb54vbxPLGHSZm4rR:B1tfuqDucFKk:C; __bid_n=1876a541bb950982184207; ariaDefaultTheme=undefined; BAIDU_WISE_UID=wapp_1686110042991_65; zhishiTopicRequestTime=1686536740997; baikeVisitId=6512a2b2-1304-4a37-9588-c91ea44a44ed; X_ST_FLOW=0; Hm_lvt_55b574651fcae74b0a9f1cf9c8d7c93a=1685600118,1685676760,1686272777,1686536757; FPTOKEN=CrIRTTfU3G5DDbb0SVFeW/1HGRRcraiTEkb2F2pfpYq0Ys1o/EPg2vKNGIrHxx3xBlrbFeEWw8Xj5N/gq+24F/5j6NCOzLG4t86EUk+yJyVOgPJc0silbR7GRhUNr9brISzB+cBPHNo7oKOQpLbO9pIQogSoWabXKIGCvWRtg1U1R/2RMQ9kkKZZbPcd3ZnOsSqwImVXyJsXTUqBdmth/jmZe8JvrfRjUBvF9mnrtZpdXVdw5WQFukUeM/eADUhcyuPjXSt+bFnkV8F7wur1kqRTBVMIJooAQwlDeS4P791c+DUGXxF9yaXXvlw3jAD9h6NcHGvwyNVwxRmHNM/P6wKtUKf9G48yy03303x7Tl5gjlIEF2KA9SIMyohx35BLhpL76c2IdLXnuPEPdNbJgw==|xW4gEfxxY/3wXM7z6aSoSlODw8ZMIgo3NNk2OAPmVaY=|10|c5ccc6091d91c1a4118e382ad36aef64; BK_SEARCHLOG=%7B%22key%22%3A%5B%22%E7%81%AB%E5%BD%B1%E5%BF%8D%E8%80%85%22%5D%7D; Hm_lpvt_55b574651fcae74b0a9f1cf9c8d7c93a=1686536846; RT=\"z=1&dm=baidu.com&si=8c16bf92-b08b-40cf-98fc-441e52a522cb&ss=lis8fga5&sl=b&tt=d45&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf\"; ab_sr=1.0.1_MTg5NmY2ZGEzNjNhZGI5OTYwNWY2MjEwMjQyNTg5ZDExM2U1YzUyNzgyMzcxZDNmZmI1ZTk3NDI0Zjk5NDg0ODNiMzgyY2M0OTYwYWZkZDg3NThjZjY3OGJiNWYzNmRkY2RiYzc5MDEwNjM5MWIzMWQ5OGM3ZTFkODZkODI5NTlmZTM5MTI0OGQxYWMxMTg0YjI4ZGE2OGMwZWExZGQyMDI4MTU3NGFjZjVmYjk5Yzk4YzI1NTJhNjc2YjUyZjZi");
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    }

    public BaiKeApi() {
        super();
        log.info("开始加载 百度百科 插件~");

        addCommand("百度百科", "进行百度百科搜索~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
            handleMessageEvent(qqMessage);
            return true;
        });
    }

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
            // 先获取百度百科的lemmaId
            String suggestUrl = String.format(suggest, msg);
            String suggestRes = HttpUtil.get(suggestUrl);
            JSONObject jsonObject = JSONObject.parseObject(suggestRes);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            URL url = new URL(api + URLEncoder.encode(msg, "UTF-8") + "/" + jsonArray.getJSONObject(0).get("lemmaId"));
            // 调用百度百科
            String html = IOUtil.sendAndGetResponseString(url, "GET", BaiKeApi.map, null);
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
                    String image = map.get("image");
                    String[] images = image.split("-");
                    if (images.length > 1){
                        image = images[0];
                    }
                    builder.append(qqMessage.uploadImage(new URL(image)));
                } catch (FileUploadException | MalformedURLException e) {
                    builder.append("图片未找到！");
                }
            }
            qqMessage.putReplyMessage(builder.build());
        }
        return qqMessage.getReplyMessages();
    }
}
