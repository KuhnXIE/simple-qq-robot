package com.shr25.robot.qq.model.dict;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.dict.TsundereDicService;
import com.shr25.robot.utils.ListUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 傲娇词典
* @TableName tsundere_dic
*/
@Data
@TableName("tsundere_dic")
@Component
public class TsundereDic extends AbstractDict{

    @Autowired
    @TableField(exist = false)
    private QqConfig qqConfig;
    @Autowired
    @TableField(exist = false)
    private TsundereDicService tsundereDicService;

    private static final String type ="傲娇";

    @Override
    public String type() {
        return type;
    }

    public static Map<String, List<TsundereDic>> tsundereDicMap = new HashMap<>();

    /**
     * 回复根据傲娇词典
     * @param qqMessage
     */
    @Override
    public void chat(QqMessage qqMessage){
        List<TsundereDic> tsundereDics = tsundereDicMap.get(qqMessage.getContent());
        TsundereDic tsundereDic = ListUtil.getRandomElement(tsundereDics);

        if (tsundereDic == null){
            return;
        }
        String reply = tsundereDic.getReply();
        reply = reply.replaceAll("\\{me}", qqConfig.getName()).replaceAll("\\{name}", qqMessage.getSender().getNick());
        String[] messages = reply.split("\\{segment}");
        for (String message : messages) {
            qqMessage.putReplyMessage(message);
        }
    }

    @Override
    public void add(String keyWord, String reply) {
        TsundereDic tsundereDic = new TsundereDic();
        tsundereDic.setKeyWord(keyWord);
        tsundereDic.setReply(reply);
        tsundereDicService.save(tsundereDic);
        List<TsundereDic> tsundereDics = tsundereDicMap.get(keyWord);
        if (tsundereDics == null){
            tsundereDics = new ArrayList<>();
            tsundereDics.add(tsundereDic);
            tsundereDicMap.put(keyWord, tsundereDics);
        }
    }
}
