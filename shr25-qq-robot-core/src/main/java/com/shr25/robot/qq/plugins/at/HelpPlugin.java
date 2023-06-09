package com.shr25.robot.qq.plugins.at;

import com.shr25.robot.api.AbstractApiMessage;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.common.SimCommand;
import com.shr25.robot.music.MusicSource;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.RobotManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 菜单工具
 */
@Service
public class HelpPlugin extends AtPlugin {

    @Autowired
    private QqConfig qqConfig;
    @Autowired
    private RobotManagerService robotManagerService;

    public HelpPlugin() {
        super();
        log.info("开始加载 菜单 插件~");
        setName("菜单插件");
        addDesc("设置聊天参数信息！");

        addCommand(AbstractApiMessage.name, "获取接口信息~", RobotMsgPermission.ALL, qqMessage -> {
            getSimMenu(qqMessage, AbstractApiMessage.name);
            return true;
        });

        addCommand(MusicSource.name, "获取音乐信息~", RobotMsgPermission.ALL, qqMessage -> {
            getSimMenu(qqMessage, MusicSource.name);
            return true;
        });

        addCommand("菜单", "获取菜单信息~", RobotMsgPermission.ALL, qqMessage -> {
            getAtMenu(qqMessage);
            return true;
        });
    }

    /**
     * 获取自定义@功能的指令列表
     */
    private void getAtMenu(QqMessage qqMessage) {
        StringBuilder strMsg = new StringBuilder("我是" + qqConfig.getName() + "！~~~\n@指令列表：\n");
        int n = 0;
        if (qqMessage.isManager()) {
            n++;
            strMsg.append(n + "、#help    所有的管理插件命令\n");
        }
        for (Map.Entry<String, String> entry : robotManagerService.getAtPluginDescMap().entrySet()) {
            n++;
            strMsg.append(n).append("、").append(entry.getKey()).append("   ").append(entry.getValue()).append('\n');
        }

        qqMessage.putReplyMessage(strMsg.toString());
    }

    /**
     * 获取普通功能的指令列表
     */
    private void getSimMenu(QqMessage qqMessage, String name) {
        StringBuilder strMsg = new StringBuilder("我是" + qqConfig.getName() + "！~~~\n"+ name +"指令列表：\n");
        int n = 0;
        List<SimCommand> simPluginDescMap = robotManagerService.getSimPluginDescMap(name);
        if (simPluginDescMap == null){
            qqMessage.putReplyMessage("不存在该指令分类~");
            return;
        }
        for (SimCommand simCommand : simPluginDescMap) {
            n++;
            strMsg.append(n).append("、").append(simCommand.getCommandStr()).append("   ").append(simCommand.getDesc()).append('\n');
        }

        qqMessage.putReplyMessage(strMsg.toString());
    }

}
