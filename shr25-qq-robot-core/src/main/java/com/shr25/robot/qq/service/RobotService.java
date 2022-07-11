package com.shr25.robot.qq.service;

import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.event.EventListeningHandle;
import com.shr25.robot.qq.util.DeviceUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoginSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 机器人服务类
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Service
@Slf4j
public class RobotService {

    @Autowired
    private EventListeningHandle eventListeningService;

    @Autowired
    private QqConfig qqConfig;

    @PostConstruct
    public void init(){
        // 启动机器人
        Thread qqRunThread = new Thread(() -> {
            start(qqConfig.getQq(), qqConfig.getPassword(), null);
        });
        qqRunThread.setDaemon(true);
        qqRunThread.setName("QQ机器人服务运行线程:" + qqConfig.getQq());
        qqRunThread.start();
    }

    /**
     * 启动qq机器人
     *
     * @param qq       qq号
     * @param password qq号对应明文密码
     */
    public void start(Long qq, String password, LoginSolver loginSolver) {
        final Bot bot = BotFactory.INSTANCE.newBot(qq, password, new BotConfiguration() {
            {
                // 加载设备信息
                this.loadDeviceInfoJson(DeviceUtil.getDeviceInfoJson(qq));
                // 使用安卓平板协议
                this.setProtocol(MiraiProtocol.ANDROID_PAD);
                // 工作空间目录，为根目录加登录的qq
                this.setCacheDir(new File(qqConfig.getWorkspace() + File.separator + "qq" + File.separator + qq));
                // 开启所有列表缓存
                // this.enableContactCache();
                // 自定义缓存
                ContactListCache cache = new ContactListCache();
                // 开启好友列表缓存
                cache.setFriendListCacheEnabled(false);
                // 关闭群成员列表缓存, 开启可能导致无法判断群管理员
                cache.setGroupMemberListCacheEnabled(false);
                // 可选设置有更新时的保存时间间隔, 默认 60 秒
                cache.setSaveIntervalMillis(60000);
                this.setContactListCache(cache);
                if(loginSolver != null){
                    this.setLoginSolver(loginSolver);
                }
                if (!qqConfig.isLogOut()) {
                    // 关闭日志输出
                    this.noBotLog();
                    this.noNetworkLog();
                }
            }
        });

        // 注册QQ机器人事件监听
        bot.getEventChannel().registerListenerHost(eventListeningService);

        // 登录QQ
        bot.login();
        // 阻塞当前线程直到 bot 离线
//      bot.join();

    }
}
