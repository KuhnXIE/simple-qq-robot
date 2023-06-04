package com.shr25.robot.common;

import com.shr25.robot.qq.model.QqMessage;

public interface ExecuteMessage {
    /**
     * 处理消息
     */
    boolean executeMessage(QqMessage qqMessage);
}
