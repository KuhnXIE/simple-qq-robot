package com.shr25.robot.common;

import com.shr25.robot.qq.model.QqMessage;

/**
 * 普通消息处理
 */
public interface ExecuteSimMessage {
    /**
     * 处理消息
     */
    boolean executeMessage(QqMessage qqMessage);
}
