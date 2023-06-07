package com.shr25.robot.qq.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.common.RobotMsgType;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.util.MessageUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * qq消息体
 *
 * @author huobing
 * @date 2022-6-13 19:10
 */
@Slf4j
@Getter
@Setter
public class QqMessage {
  /** 接收消息的机器人QQ */
  private Long botId = null;

  /** 消息发送者QQ */
  private Long senderId = null;

  /** 发消息的群号 */
  private Long groupId = null;

  /** 发消息的群名称 */
  private String groupName = null;

  /** 发送者 */
  private User sender = null;

  /** 联系对象 */
  private Contact contact = null;

  /** 发生消息的QQ群 */
  private Group group = null;

  /** 角色 */
  private RobotMsgPermission robotMsgPermission;

  /** 消息类型 */
  private RobotMsgType robotMsgType;

  /** at */
  private Boolean at = false;

  /** 指令 */
  String command;

  /** 消息体 */
  private String content = null;

  /** 回复的消息 */
  private Multimap<Contact, Message> replyMessages;

  /** 消息事件 */
  private Event event;

  /** 是否继续执行 */
  private boolean executeNext = true;

  public boolean isExecuteNext() {
    return executeNext && replyMessages.isEmpty();
  }

  public QqMessage(Event event, QqConfig qqConfig, Set<Long> normalManage) {
    this.event = event;
    this.replyMessages = ArrayListMultimap.create();

    if(event instanceof GroupMemberEvent) {
      group = ((GroupMemberEvent) event).getGroup();
      contact = group;
      senderId = ((GroupMemberEvent) event).getMember().getId();
      groupId = contact.getId();
      groupName = group.getName();
    }else if (event instanceof MessageEvent){
      botId = ((MessageEvent) this.getEvent()).getBot().getId();
      content = getTextContent();
      sender = ((MessageEvent) event).getSender();
      senderId = sender.getId();

      if(event instanceof GroupMessageEvent){
        group = ((GroupMessageEvent) event).getGroup();
        contact = group;
        groupId =  contact.getId();
        groupName = group.getName();
      }else if(event instanceof GroupMessageSyncEvent){
        group = ((GroupMessageSyncEvent) event).getGroup();
        contact = group;
        groupId =  contact.getId();
        groupName = group.getName();
      }else{
        contact = sender;
      }

      if(event instanceof GroupMessageEvent || event instanceof GroupMessageSyncEvent){
        if(at){
          robotMsgType = RobotMsgType.GroupAtBot;
        }else{
          robotMsgType = RobotMsgType.Group;
        }
      }else if(event instanceof GroupTempMessageEvent){
        robotMsgType = RobotMsgType.GroupTemp;
      }else if(event instanceof FriendMessageEvent){
        robotMsgType = RobotMsgType.Friend;
      }else if(event instanceof StrangerMessageEvent){
        robotMsgType = RobotMsgType.Strange;
      }
    }

    if(event instanceof GroupMessageSyncEvent){
      robotMsgPermission = RobotMsgPermission.SYSTEM;
    }else if(isRootManageQQ(senderId, qqConfig)){
      robotMsgPermission = RobotMsgPermission.SYSTEM;
    }else if(normalManage.contains(senderId)){
      robotMsgPermission = RobotMsgPermission.ADMIN;
    }else{
      MemberPermission permission = null;
      // 群主或者管理员
      if (event instanceof GroupMessageEvent) {
        permission = ((GroupMessageEvent) event).getPermission();
        log.info("-在QQ群-{}-中--{}--发消息--群权限：{}", groupId, senderId, permission);
      }else if (event instanceof  GroupMemberEvent){
        permission = ((GroupMemberEvent) event).getMember().getPermission();
      }

      if(permission != null){
        if (MemberPermission.OWNER.equals(permission)) {
          robotMsgPermission = RobotMsgPermission.OWNER;
        }else if(MemberPermission.ADMINISTRATOR.equals(permission)){
          robotMsgPermission = RobotMsgPermission.ADMINISTRATOR;
        } else{
          robotMsgPermission = RobotMsgPermission.MEMBER;
        }
      }else{
        robotMsgPermission = RobotMsgPermission.MEMBER;
      }
    }
  }

  /**
   * 获取命令
   */
  public String getCommand(String msg){
    String command = null;
    String pattern = "^[#|/](\\S+)(\\s+(.*))?";

    // 创建 Pattern 对象
    Pattern r = Pattern.compile(pattern);

    if(msg != null) {
      // 现在创建 matcher 对象
      Matcher m = r.matcher(msg);
      if (m.find()) {
        command = m.group(1);
      }
    }
    return command;
  }

  /**
   * 获取 命令后的参数
   * @return
   */
  public String getParameter(){
    if(getContent() != null) {
      return getContent().trim();
    }else{
      return "";
    }
  }

  /**
   * 获取接受的消息体
   * @return
   */
  public MessageChainBuilder getMessage(){
    // 接收的字符串消息
    MessageChainBuilder builder = MessageUtil.createBuilder();
    getMessageEvent().getMessage().forEach(singleMessage -> {
      if(singleMessage instanceof MessageContent) {
        // 过滤艾特消息
        if (singleMessage instanceof At) {
        } else if (singleMessage instanceof PlainText) {
          if (StringUtils.isNotBlank(command) && builder.isEmpty()) {
            builder.append(new PlainText(singleMessage.contentToString().substring(command.length() + 1).trim()));
          } else {
            builder.append(singleMessage);
          }
        } else {
          builder.append(singleMessage);
        }
      }
    });
    return builder;
  }

  /**
   * 获取文本内容
   * @return
   */
  public String getTextContent(){
    // 接收的字符串消息
    StringBuilder strMsg = new StringBuilder();
    if (event instanceof GroupMessageSyncEvent) {
      getMessageEvent().getMessage().forEach(singleMessage -> {
        // 过滤艾特消息
        if (singleMessage instanceof At) {
          if (((At) singleMessage).getTarget() == botId) {
            at = true;
          }
        }else if(singleMessage instanceof PlainText){
          strMsg.append(singleMessage.contentToString().trim());
        }
      });
    }else if (event instanceof GroupMessageEvent) {
      getGroupMessageEvent().getMessage().forEach(singleMessage -> {
        // 过滤艾特消息
        if (singleMessage instanceof At) {
          if (((At) singleMessage).getTarget() == botId) {
            at = true;
          }
        }else if(singleMessage instanceof PlainText){
          strMsg.append(singleMessage.contentToString().trim());
        }
      });
    }else{
      getMessageEvent().getMessage().forEach(singleMessage -> {
        // 过滤艾特消息
        if (!(singleMessage instanceof At)) {
          strMsg.append(singleMessage.contentToString().trim());
        }
      });
    }

    command = getCommand(strMsg.toString().trim());
    if(StringUtils.isNotBlank(command)){
      return strMsg.substring(command.length()+1).trim();
    }else{
      return strMsg.toString().trim();
    }

  }

  /** 是否是超级管理员 */
  public boolean isSuperManager(){
    return robotMsgPermission.getPermission() <= RobotMsgPermission.SYSTEM.getPermission();
  }

  /** 是否是管理员 */
  public boolean isManager(){
    return robotMsgPermission.getPermission() <= RobotMsgPermission.ADMIN.getPermission();
  }

  /** 是否可以操作群 */
  public boolean isCanOperatorGroup(){
    return groupId != null && robotMsgPermission.getPermission() <= RobotMsgPermission.ADMINISTRATOR.getPermission();
  }

  /**
   * 是否超级管理员
   *
   * @param qq
   * @return
   */
  private boolean isRootManageQQ(Long qq, QqConfig qqConfig) {
    return qqConfig.getRootManageQq().contains(qq);
  }

  public MessageEvent getMessageEvent() {
    return (MessageEvent) this.getEvent();
  }

  public GroupMemberEvent getGroupMemberEvent() {
    return (GroupMemberEvent) this.getEvent();
  }

  public MemberJoinEvent getMemberJoinEvent() {
    return (MemberJoinEvent) this.getEvent();
  }

  public MemberJoinEvent.Invite getInviteMemberJoinEvent() {
    return (MemberJoinEvent.Invite) this.getEvent();
  }

  public MemberJoinEvent.Active getActiveMemberJoinEvent() {
    return (MemberJoinEvent.Active) this.getEvent();
  }

  public MemberLeaveEvent.Kick getKickMemberLeaveEvent() {
    return (MemberLeaveEvent.Kick) this.getEvent();
  }

  public MemberLeaveEvent.Quit getQuitMemberLeaveEvent() {
    return (MemberLeaveEvent.Quit) this.getEvent();
  }

  public MessageEvent getFriendMessageEvent() {
    return (FriendMessageEvent) this.getEvent();
  }

  public GroupMessageEvent getGroupMessageEvent() {
    return (GroupMessageEvent) this.getEvent();
  }

  /**
   * 添加消息
   *
   * @param contact 联系人
   * @param message 消息体
   */
  public void putReplyMessage(Contact contact, Message message) {
    this.replyMessages.put(contact, message);
  }

  /**
   * 添加消息
   *
   * @param contact 联系人
   * @param message 消息字符串
   */
  public void putReplyMessage(Contact contact, String message) {
    this.putReplyMessage(contact, new PlainText(message));
  }

  /**
   * 添加消息
   *
   * @param contact 联系人
   * @param builder {@link MessageChainBuilder}
   */
  public void putReplyMessage(Contact contact, MessageChainBuilder builder) {
    this.putReplyMessage(contact, builder.build());
  }

  /**
   * 添加消息，自动判断发送的用户
   *
   * @param message 消息体
   */
  public void putReplyMessage(Message message) {
    this.putReplyMessage(contact, message);
  }

  /**
   * 添加消息，自动判断发送的用户
   *
   * @param message 消息字符串
   */
  public void putReplyMessage(String message) {
    this.putReplyMessage(new PlainText(message));
  }

  /**
   * 添加消息，自动判断发送的用户
   *
   * @param builder {@link MessageChainBuilder}
   */
  public void putReplyMessage(MessageChainBuilder builder) {
    this.putReplyMessage(builder.build());
  }

  /**
   * 清楚所有消息
   */
  public void clearReplyMessage() {
    this.replyMessages.clear();
  }

  /**
   * 直接发送消息
   * @param message
   */
  public void sendMessage(Message message){
    contact.sendMessage(message);
  }

  /**
   * 直接发送消息
   * @param message
   */
  public void sendMessage(String message){
    sendMessage(new PlainText(message));
  }
  /**
   * 是否系统机器人
   * @return
   */
  public boolean isSysBot(){
    return senderId > 2854190000l && senderId < 2854200000l;
  }
}
