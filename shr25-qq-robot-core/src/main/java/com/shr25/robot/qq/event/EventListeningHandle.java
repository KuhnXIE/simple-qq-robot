package com.shr25.robot.qq.event;

import com.shr25.robot.qq.service.RobotManagerService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Slf4j
@Component
public class EventListeningHandle extends SimpleListenerHost {

    @Autowired
    private RobotManagerService robotManagerService;

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
        this.publishMessage(event);
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
}
