package com.shr25.robot.qq.service.chatGpt;


import com.shr25.robot.qq.exception.ChatException;
import com.shr25.robot.qq.model.Vo.ChatVo;

/**
 * 交互服务
 *
 */
public interface InteractService {
    /**
     * 聊天
     *
     * @param chatVo 聊天BO
     * @return {@link String}
     * @throws ChatException 聊天异常
     */
    String chat(ChatVo chatVo) throws ChatException;
}
