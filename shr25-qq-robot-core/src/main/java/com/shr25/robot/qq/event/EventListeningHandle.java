package com.shr25.robot.qq.event;

import com.shr25.robot.qq.exception.ChatException;
import com.shr25.robot.qq.model.Vo.ChatVo;
import com.shr25.robot.qq.service.RobotManagerService;
import com.shr25.robot.qq.service.chatGpt.InteractService;
import com.shr25.robot.qq.util.ChatBotUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Slf4j
@Component
public class EventListeningHandle extends SimpleListenerHost {

    @Autowired
    private RobotManagerService robotManagerService;
    @Resource
    private InteractService interactService;
    private static final String RESET_WORD = "重置会话";

    /**
     * 监听陌生人消息
     *
     * @param event 陌生人消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(StrangerMessageEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }
    /**
     * 监听群临时会话消息
     *
     * @param event 群临时会话消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(GroupTempMessageEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }


    /**
     * 监听好友消息
     *
     * @param event 好友消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onFriendMessageEvent(FriendMessageEvent event) {
//        this.publishMessage(event);

        ChatVo chatVo = new ChatVo();
        chatVo.setSessionId(String.valueOf(event.getSubject().getId()));
        String prompt = event.getMessage().contentToString().trim();
        response(event, chatVo, prompt);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听群消息
     *
     * @param event 群消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(GroupMessageEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 接受自己发的消息
     *
     * @param event 陌生人消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(GroupMessageSyncEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听入群消息
     *
     * @param event 群消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(MemberJoinEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听退群消息
     *
     * @param event 群消息
     * @return {@link ListeningStatus}
     */
    @EventHandler
    public ListeningStatus onMessageEvent(MemberLeaveEvent event) {
        this.publishMessage(event);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    private void publishMessage(BotEvent event) {
        try {
            robotManagerService.publishMessage(event);
        } catch (Exception e) {
            log.error(String.format("发送消息失败:%s", e.getMessage()), e);
        }
    }

    /**
     * 私聊回复chatGpt消息
     */
    private void response(@NotNull MessageEvent event, ChatVo chatVo, String prompt) {
        if (RESET_WORD.equals(prompt)) {
            //检测到重置会话指令
            ChatBotUtil.resetPrompt(chatVo.getSessionId());
            event.getSubject().sendMessage("重置会话成功");
        } else {
            String response;
            try {
                chatVo.setPrompt(prompt);
                response = interactService.chat(chatVo);
            }catch (ChatException e){
                response = e.getMessage();
            }
            try {
                MessageChain messages = new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append(response)
                        .build();
                event.getSubject().sendMessage(messages);
            }catch (MessageTooLargeException e){
                //信息太大，无法引用，采用直接回复
                event.getSubject().sendMessage(response);
            }
        }
    }


}
