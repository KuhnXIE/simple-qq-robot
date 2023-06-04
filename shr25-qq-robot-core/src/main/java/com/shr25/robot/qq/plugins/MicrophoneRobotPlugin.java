package com.shr25.robot.qq.plugins;

import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.model.QqGroupInfo;
import com.shr25.robot.qq.model.QqGroupMessage;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.qqGroup.IQqGroupInfoService;
import com.shr25.robot.qq.service.qqGroup.IQqGroupMessageService;
import com.shr25.robot.qq.util.MessageUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 传声通插件
 *
 * @author huobing
 * @date 2022-6-15 14:33
 */
@Component
public class MicrophoneRobotPlugin  extends RobotPlugin {
  @Autowired
  IQqGroupMessageService qqGroupMessageService;

  @Autowired
  IQqGroupInfoService qqGroupInfoService;

  public MicrophoneRobotPlugin() {
    super();
    log.info("开始加载“传声筒插件V2.0”");
    setName("传声筒插件");
    addDesc("在多个群同步发送的消息");
    addCommand("所有群", "对机器人所有加入的群进行群发消息", RobotMsgPermission.ADMIN, qqMessage -> {
      MessageChainBuilder builder = qqMessage.getMessage();
      MessageUtil.getAllGroups().forEach(contact -> {
        qqMessage.putReplyMessage(contact, builder);
      });
      return true;
    });
    setSort(1001);
  }

  @Override
  public Set<String> getAllCommands(QqMessage qqMessage){
    Set<String> commands = super.getAllCommands(qqMessage);

    if(qqMessage.getGroupId() != null){
      QqGroupInfo qqGroupInfo = qqGroupInfoService.query().eq("group_id", qqMessage.getGroupId()).one();
      if(qqGroupInfo != null && StringUtils.isNotBlank(qqGroupInfo.getKeyword())){
        String[] keywords = qqGroupInfo.getKeyword().split(",");
        if(keywords.length>3){
          for (int i = 1; i <= keywords.length-2; i++) {
            commands.add("#"+keywords[i]+"{需要群发的消息}"+ "    " +"群发消息到“"+keywords[i]+"“群");
          }
        }
        return commands;
      }
    }
    return commands;
  }

  @Override
  public boolean executeMessage(QqMessage qqMessage) {
    return batchSend(qqMessage);
  }

  public boolean batchSend(QqMessage qqMessage) {
    if(StringUtils.isNotBlank(qqMessage.getCommand())){
        if(qqMessage.isManager()) {
          List<QqGroupInfo> qqGroupInfoList = qqGroupInfoService.query().like("keyword", qqMessage.getCommand()).list();
          //所有监听关键字的群
          List<Group> groups = new ArrayList<>();
          //群是否监听关键字
          if (qqGroupInfoList != null) {
            qqGroupInfoList.forEach(item -> {
              Group group = MessageUtil.getGroup(item.getGroupId());
              if (group != null) {
                groups.add(group);
              }
            });

            if (groups.size() > 0) {
              MessageChainBuilder builder = qqMessage.getMessage();
              groups.forEach(contact -> {
                qqMessage.putReplyMessage(contact, builder);
              });
              saveMessage(qqMessage, qqMessage.getCommand());
            }
          }
        }
    }
    return true;
  }

  public void saveMessage(QqMessage qqMessage, String command) {
    QqGroupMessage qqGroupMessage = new QqGroupMessage();
    qqGroupMessage.setGroupId(qqMessage.getGroupId());
    qqGroupMessage.setQq(qqMessage.getSender().getId());
    qqGroupMessage.setQqName(qqMessage.getSender().getNick());
    qqGroupMessage.setKeyword(command);
    qqGroupMessage.setMessage(qqMessage.getParameter());
    qqGroupMessageService.save(qqGroupMessage);
  }
}
