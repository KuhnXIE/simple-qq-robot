package com.shr25.robot.qq.model.dict;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.dict.CuteDicService;
import com.shr25.robot.utils.ListUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* 可爱词典
* @TableName cute_dic
*/
@Data
@TableName("cute_dic")
@Component
public class CuteDic extends AbstractDict{

    @Autowired
    @TableField(exist = false)
    private QqConfig qqConfig;
    @Autowired
    @TableField(exist = false)
    private CuteDicService cuteDicService;

    public static Map<String, List<CuteDic>> cuteMap = new HashMap<>();

    private static final String type ="可爱";

    @Override
    public String type() {
        return type;
    }

    /**
     * 回复根据可爱字典
     * @param qqMessage
     */
    @Override
    public void chat(QqMessage qqMessage){
        List<CuteDic> cuteDics = cuteMap.get(qqMessage.getContent());
        CuteDic cuteDic = ListUtil.getRandomElement(cuteDics);

        if (cuteDic == null){
            return;
        }
        String reply = cuteDic.getReply();
        reply = reply.replaceAll("\\{me}", qqConfig.getName()).replaceAll("\\{name}", qqMessage.getSender().getNick());
        String[] messages = reply.split("\\{segment}");
        for (String message : messages) {
            qqMessage.putReplyMessage(message);
        }
    }

    @Override
    public void add(String keyWord, String reply) {
        CuteDic cuteDic = new CuteDic();
        cuteDic.setKeyWord(keyWord);
        cuteDic.setReply(reply);
        cuteDicService.save(cuteDic);
        List<CuteDic> cuteDics = cuteMap.get(keyWord);
        if (cuteDics == null){
            cuteDics = new ArrayList<>();
            cuteDics.add(cuteDic);
            cuteMap.put(keyWord, cuteDics);
        }
    }
}
