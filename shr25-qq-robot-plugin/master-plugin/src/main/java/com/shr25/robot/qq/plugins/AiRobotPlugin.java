package com.shr25.robot.qq.plugins;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.util.MessageUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI机器人插件
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Component
public class AiRobotPlugin extends RobotPlugin {
    @Autowired
    QqConfig qqConfig;

    public AiRobotPlugin() {
        super();
        setName("AI机器人插件");
        addDesc("接入青云客ai机器人，默认最后执行，前面没有回复消息的时候才执行");
        addCommand("天气 {城市}", "查询天气预报信息，示例：天气 深圳", true);
        addCommand("归属 {手机或IP}", "手机、ＩＰ地址归属，示例：归属 13333333333");
        addCommand("邮编 {邮编号码}", "邮政编码及地区查询 ");
        addCommand("计算 {表达式}", "计算简单的算术，示例：计算 15+13");
        addCommand("成语 {成语}", "查询成语介绍，示例：成语 一心一意");
        addCommand("歌词 {歌曲名称}", "按歌曲名称查询歌词，示例：歌词 歌曲名称");
        addCommand("翻译 {语句}", "中译英、英译中，示例：翻译 i love you", true);
        addCommand("星座 {星座}", "查询星座今日运势，示例：星座 天秤座");
        addCommand("星座 {星座}", "查询星座今日运势，示例：星座 天秤座");
        addCommand("星座 {星座}", "查询星座今日运势，示例：星座 天秤座");
        addCommand("{星座}", "查询星座介绍");
        addCommand("{汉字}字", "汉字五笔拼音笔画查询");
        addCommand("笑话", "返回一条笑话");
        setSort(Integer.MAX_VALUE);
    }

    @Override
    public boolean executeGroupMessage(QqMessage qqMessage) {
        // 消息为空、好友消息、群里被@
        if (!StringUtil.isBlank(qqMessage.getContent())) {
            //排除机器人号段
            if (!qqMessage.isSysBot()) {
                // 记录机器人是否被艾特
                AtomicBoolean flag = new AtomicBoolean(false);
                // 接收的字符串消息
                StringBuilder strMsg = new StringBuilder();
                if (qqMessage.getEvent() instanceof GroupMessageEvent) {
                    qqMessage.getGroupMessageEvent().getMessage().forEach(singleMessage -> {
                        // 过滤艾特消息
                        if (singleMessage instanceof At) {
                            if (((At) singleMessage).getTarget() == qqMessage.getMessageEvent().getBot().getId()) {
                                flag.set(true);
                            }
                        } else {
                            strMsg.append(singleMessage.contentToString().trim());
                        }
                    });
                }
                // 如果是群消息，则需要被@才回复消息
                if (flag.get()) {
                    MessageChainBuilder builder = MessageUtil.createBuilder();
                    builder.append(new At(qqMessage.getSenderId()));
                    if (strMsg.length() == 0) {
                        builder.append(new PlainText(" 艾特我不说话，是想干嘛"));
                    } else {
                        builder.append(new PlainText(" " + this.qingYunKe(strMsg.toString())));
                    }
                    qqMessage.putReplyMessage(builder.build());
                }
            }
        }

        return true;
    }

    public boolean executeFriendMessage(QqMessage qqMessage){
        if (StringUtil.isBlank(qqMessage.getContent())) {
            qqMessage.putReplyMessage("你不说话，是想干嘛");
        } else {
            qqMessage.putReplyMessage(this.qingYunKe(qqMessage.getContent()));
        }
        return true;
    }

    /**
     * https://api.qingyunke.com/
     * 青云客ai机器人
     */
    public String qingYunKe(String msg) {
        String url = "http://api.qingyunke.com/api.php";
        try {
            HttpResponse httpResponse = HttpRequest.get(url)
                    // 必需，固定值
                    .form("key", "free")
                    // 可选，0表示智能识别
                    .form("appid", "0")
                    // 必需，关键词，提交前请先经过 urlencode 处理
                    .form("msg", URLEncoder.encode(msg, "UTF-8"))
                    .execute();
            return StringEscapeUtils.unescapeHtml4(JSONUtil.parseObj(httpResponse.body()).getStr("content").replaceAll("菲菲", qqConfig.getName()).replaceAll("\\{br}", "\n"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
