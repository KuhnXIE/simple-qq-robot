package com.shr25.robot.qq.plugins;

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
    setName("传声筒插件");
    addDesc("在多个群同步发送的消息");
    setSort(1001);
  }

  @Override
  public Set<String> getAllCommands(QqMessage qqMessage){
    Set<String> commands = new TreeSet<>();
    if(qqMessage.getGroupId() != null){
      QqGroupInfo qqGroupInfo = qqGroupInfoService.query().eq("group_id", qqMessage.getGroupId()).one();
      if(qqGroupInfo != null && StringUtils.isNotBlank(qqGroupInfo.getKeyword())){
        String[] keywords = qqGroupInfo.getKeyword().split(",");
        if(keywords.length>3){
          for (int i = 1; i <= keywords.length-2; i++) {
            addCommand(commands, "#"+keywords[i]+"{需要群发的消息}", "群发消息到“"+keywords[i]+"“群");
          }
        }
        return commands;
      }
    }
    if(qqMessage.isManager()){
      addCommand(commands, "#所有群", "对机器人所有加入的群进行群发消息");
    }
    return commands;

  }

  @Override
  public boolean executeGroupMessage(QqMessage qqMessage) {
    String command = qqMessage.getCommand();
    if(command != null){
      if(qqMessage.isCanOperatorGroup()) {
        List<QqGroupInfo> qqGroupInfoList = qqGroupInfoService.query().like("keyword", command).list();
        //所有监听关键字的群
        List<Group> groups = new ArrayList<>();
        //群是否监听关键字
        AtomicBoolean isCan = new AtomicBoolean(false);
        if (qqGroupInfoList != null) {
          qqGroupInfoList.forEach(item -> {
            if (item.getGroupId().equals(qqMessage.getGroupId())) {
              isCan.set(true);
            } else {
              Group group = MessageUtil.getGroup(item.getGroupId());
              if (group != null) {
                groups.add(group);
              }
            }
          });

          if (groups.size() > 0 && isCan.get()) {
            MessageChainBuilder builder = qqMessage.getMessage("#"+command);
            groups.forEach(contact -> {
              qqMessage.putReplyMessage(contact, builder);
            });
            saveMessage(qqMessage, command);
          }
        }
      }
    }

    return true;
  }

  @Override
  public boolean executeFriendMessage(QqMessage qqMessage) {
    String command = qqMessage.getCommand();
    if(command != null){
      if (qqMessage.getContent().startsWith("#所有群")) {
        if (qqMessage.isManager()) {
          MessageChainBuilder builder = qqMessage.getMessage("#所有群");
          MessageUtil.getAllGroups().forEach(contact -> {
            qqMessage.putReplyMessage(contact, builder);
          });
        }
      } else {
        if(qqMessage.isManager()) {
          List<QqGroupInfo> qqGroupInfoList = qqGroupInfoService.query().like("keyword", command).list();
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
              MessageChainBuilder builder = qqMessage.getMessage("#"+command);
              groups.forEach(contact -> {
                qqMessage.putReplyMessage(contact, builder);
              });
              saveMessage(qqMessage, command);
            }
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
    qqGroupMessage.setMessage(qqMessage.getParameter("#"+command));
    qqGroupMessageService.save(qqGroupMessage);
  }
}
