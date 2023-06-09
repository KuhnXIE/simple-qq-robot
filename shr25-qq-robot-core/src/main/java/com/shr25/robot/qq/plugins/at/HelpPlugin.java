package com.shr25.robot.qq.plugins.at;

import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.RobotManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        log.info("开始加载 聊天 插件~");
        setName("聊天插件");
        addDesc("设置聊天参数信息！");

//        addCommand("help", "获取菜单信息~", RobotMsgPermission.ADMIN, qqMessage -> {
//            getMenu(qqMessage);
//            return true;
//        });
//
//        addCommand("帮助", "获取菜单信息~", RobotMsgPermission.ADMIN, qqMessage -> {
//            getMenu(qqMessage);
//            return true;
//        });

        addCommand("菜单", "获取菜单信息~", RobotMsgPermission.ADMIN, qqMessage -> {
            getMenu(qqMessage);
            return true;
        });
    }

    /**
     * 获取自定义功能的指令列表
     */
    private void getMenu(QqMessage qqMessage) {
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
}
