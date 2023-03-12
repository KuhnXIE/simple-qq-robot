package com.shr25.robot.qq;

import com.shr25.robot.qq.event.EventListeningHandle;
import com.shr25.robot.qq.util.DeviceUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.junit.Test;

/**
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Slf4j
public class MyTest {

    private final static Long qq = 2744211324l;
    private final static String password = "dq1387475385";

    @Test
    public void DeviceInfo() {
        System.out.println();
    }
    @Test
    public void RobotTest() {
        final Bot bot = BotFactory.INSTANCE.newBot(qq, password, new BotConfiguration() {
            {
                /**
                 * 加载设备信息
                 */
                this.loadDeviceInfoJson(DeviceUtil.getDeviceInfoJson1(qq));
                /**
                 * 使用安卓平板协议
                 */
                this.setProtocol(MiraiProtocol.ANDROID_PAD);
            }
        });
        try {
            // 注册QQ机器人事件监听
            bot.getEventChannel().registerListenerHost(new EventListeningHandle());
            // 登录QQ
            bot.login();
            // 阻塞当前线程直到 bot 离线
            bot.join();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
