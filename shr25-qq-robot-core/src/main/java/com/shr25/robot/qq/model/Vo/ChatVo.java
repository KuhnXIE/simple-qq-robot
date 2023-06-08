package com.shr25.robot.qq.model.Vo;

import lombok.Data;

/**
 * 聊天BO
 *
 * @author ashinnotfound
 * @date 2023/02/01
 */
@Data
public class ChatVo {
    /**
     * 会话id
     */
    private String sessionId;
    /**
     * 问题
     */
    private String prompt;
}
