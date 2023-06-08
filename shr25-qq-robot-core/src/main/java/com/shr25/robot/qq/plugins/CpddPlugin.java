package com.shr25.robot.qq.plugins;

import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * cpdd插件
 */
@Component
public class CpddPlugin extends RobotPlugin {

    private static final Map<Long, Long> cpddMap = new HashMap<>();

    public CpddPlugin() {
        super();
        log.info("开始加载 cpdd 插件~");
        setName("cpdd插件");
        addDesc("发送cpdd信息");
        setSort(1002);

        addCommand("cpdd", "进行cpdd~", qqMessage -> {
            enabledCpdd(qqMessage);
            return true;
        }, true);

    }

    private void enabledCpdd(QqMessage qqMessage) {
        Long groupId = qqMessage.getGroupId();

        Long time = cpddMap.get(groupId);
        if (time != null) {
            // 未到cpdd时间直接跳过
            if ((time + 10 * 60 * 1000) < System.currentTimeMillis()) {
                return;
            }
        }
        // 设置为当前时间
        time = System.currentTimeMillis();
        cpddMap.put(groupId, time);
        // todo 替换中间信息
        String cpddMsg = "Cpdd" + '\n' + "饿了 不知道吃什么 想吃点爱情的苦" + '\n' + "下次cpdd时间" + DateUtils.dateFormat(new Date(time + 10 * 60 * 1000));

        qqMessage.putReplyMessage(cpddMsg);
    }
}
