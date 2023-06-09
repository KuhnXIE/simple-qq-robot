package com.shr25.robot.qq.model.dict;

import com.shr25.robot.base.BaseEntity;
import com.shr25.robot.qq.model.QqMessage;
import lombok.Data;

/**
 * 抽象词典类，标准是一个词典
 */
@Data
public abstract class AbstractDict extends BaseEntity {

    /**
     * 关键字
     */
    private String keyWord;
    /**
     * 回复
     */
    private String reply;

    public abstract String type();

    public abstract void chat(QqMessage qqMessage);

    /**
     * 学习回复
     * @param keyWord 检测的内容
     * @param reply 回复的内容
     */
    public abstract void add(String keyWord, String reply);

}
