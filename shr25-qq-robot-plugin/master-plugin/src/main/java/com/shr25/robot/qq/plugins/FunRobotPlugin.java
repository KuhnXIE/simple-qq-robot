package com.shr25.robot.qq.plugins;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.model.msg.MsgContent;
import com.shr25.robot.qq.service.msg.IMsgContentService;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 娱乐插件
 *
 * @author huobing
 * @date 2022/6/6 14:00
 */
@Component
public class FunRobotPlugin extends RobotPlugin {
    @Autowired
    IMsgContentService msgContentService;

    public FunRobotPlugin() {
        super();
        setName("娱乐插件");
        addDesc("轻松一刻，惊喜想不到！~~~~~~");
        addCommand("舔狗日记", "做一只快乐的舔狗", true);
        addCommand("毒鸡汤", "生活很苦");
        addCommand("彩虹屁", "彩虹屁生成器");
        addCommand("朋友圈", "朋友圈文案生成器");
        addCommand("一言", "没有什么是一句话搞定不了的");
        setSort(2);
    }





    public boolean executeMessage(QqMessage qqMessage) {
        AtomicBoolean executeNext = new AtomicBoolean(true);
        if (!(qqMessage.getEvent() instanceof MessageEvent)) {
            return false;
        }



        switch (qqMessage.getContent()) {
            case "舔狗日记":
                String msg = tiangou();
                qqMessage.putReplyMessage(msg);
                break;
            //case "笑话":   //使用青云客ai机器人默认回复
                //qqMessage.putReplyMessage(xiaohua());
                //break;
            case "毒鸡汤":
                qqMessage.putReplyMessage(shadiao("du"));
                break;
            case "彩虹屁":
                qqMessage.putReplyMessage(shadiao("chp"));
                break;
            case "朋友圈":
                qqMessage.putReplyMessage(shadiao("pyq"));
                break;
            case "一言":
                qqMessage.putReplyMessage(yiyan());
                break;
            default:
                if(qqMessage.getContent() != null && qqMessage.getContent().length() < 20){
                    String keyword = null;
                    if(qqMessage.getContent().equals("安慰安慰我")
                      || qqMessage.getContent().equals("安慰我")
                      || qqMessage.getContent().equals("安慰人")){
                        keyword = "安慰";
                    }else{
                        keyword = qqMessage.getContent();
                    }
                    MsgContent msgContent = msgContentService.getRandomMsg(keyword);

                    if(msgContent != null){
                        qqMessage.putReplyMessage(msgContent.getContent());
                    }
                }

                break;
        }
        return executeNext.get();
    }



    /**
     * 添狗日志
     * @return
     */
    private String tiangou(){
        List<String> list = Arrays.asList(
          "https://cloud.qqshabi.cn/api/tiangou/api.php"
//          ,"https://api.oick.cn/dog/api.php"
//          ,"https://api.ixiaowai.cn/tgrj/index.php"
        );
        String dogUrl = RandomUtil.randomEle(list);
        return HttpUtil.get(dogUrl);
    }

    /**
     * 沙雕
     * @param type
     * @return
     */
    private String shadiao(String type){
        JSONObject shadiao = JSONUtil.parseObj(HttpUtil.get("https://api.shadiao.app/"+type));
        return shadiao.getJSONObject("data").getStr("text");
    }

    /**
     * 笑话
     * @return
     */
    private String xiaohua(){
        Random r = new Random();
        int num = r.nextInt(159)+1; // 生成[0,10]区间的整数
        int page = num/20;
        int itemCount = num%20;
        if(itemCount != 0){
            page = page+1;
            itemCount = itemCount -1;
        }else{
            itemCount = 19;
        }
        Element titleElement = null;
        Element contentElement = null;
        try
        {
            Document document = Jsoup.connect("https://xiaohua.zol.com.cn/neihan/"+page+".html").get();
            Elements elements = document.getElementsByClass("article-summary");
            titleElement = elements.get(itemCount).getElementsByClass("article-title").first();
            contentElement = elements.get(itemCount).getElementsByClass("summary-text").first();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if(titleElement != null){
            return String.format("《%s》\r\n%s", titleElement.text(), contentElement.text());
        }
        return "网络繁忙！~~~~~~~~~";
    }

    /**
     * 一言
     * @return
     */
    private String yiyan(){
        JSONObject yiyan = JSONUtil.parseObj(HttpUtil.get("https://v1.hitokoto.cn/?encode=json"));
        return String.format("%s——%s「%s」"
          , yiyan.getStr("hitokoto")
          , yiyan.getStr("from_who", "")
          , yiyan.getStr("from"));
    }
}
