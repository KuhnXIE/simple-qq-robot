package com.shr25.robot.common;

import com.shr25.robot.qq.model.QqMessage;

public interface ExecuteAtMessage {
    /**
     * 处理at消息
     */
    boolean executeMessage(QqMessage qqMessage);
}
