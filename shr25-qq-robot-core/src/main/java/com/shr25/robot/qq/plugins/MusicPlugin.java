package com.shr25.robot.qq.plugins;

import com.shr25.robot.music.MusicCardProvider;
import com.shr25.robot.music.MusicFactory;
import com.shr25.robot.music.MusicInfo;
import com.shr25.robot.music.MusicSource;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.RobotManagerService;
import net.mamoe.mirai.message.data.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * cpdd插件
 */
@Component
public class MusicPlugin extends RobotPlugin {

    private static final Map<Long, Long> cpddMap = new HashMap<>();

    public MusicPlugin() {
        super();
        log.info("开始加载 点歌 插件~");
        setName("点歌插件");
        addDesc("分享点歌信息");
        setSort(1003);

        addCommand("点歌", "进行点歌~", qqMessage -> {
            shareSong(qqMessage);
            return true;
        }, false);

    }

    private void shareSong(QqMessage qqMessage) {
        // 截取后面的关键字
        String keyword = RobotManagerService.subParam(qqMessage.getContent());
        // 此处使用qq音乐搜索
        MusicSource musicSource = MusicFactory.getMusicSource("QQ音乐");
        if (musicSource == null)
            throw new IllegalArgumentException("music source not exists");
        // 此处使用默认样板
        MusicCardProvider cb = MusicFactory.getCard("Mirai");
        if (cb == null)
            throw new IllegalArgumentException("card template not exists");

        MusicInfo musicInfo;
        try {
            musicInfo = musicSource.get(keyword);
        } catch (Throwable t) {
            qqMessage.putReplyMessage("无法找到歌曲" + keyword);
            return;
        }
        try {
            Message m = cb.process(musicInfo, qqMessage.getContact());
            if (m != null) {
                qqMessage.putReplyMessage(m);
                return;
            }
        } catch (Throwable t) {
            log.error("封装音乐消息失败！");
        }
        qqMessage.putReplyMessage("分享歌曲失败。");
    }
}
