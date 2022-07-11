package com.shr25.robot.qq.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.util.MessageUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

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
@Data
public class QqMessage {
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

  /** 是否超级管理员 */
  private boolean isRoot = false;

  /** 是否普通管理员 */
  private boolean isNormalManage = false;

  /** 是否是群管理员,操作人 */
  private boolean isGroupOperator = false;

  /** 消息类型  1临时消息 2.好友消息 3.群临时消息 4.群消息  */
  private Integer messageType = 0;

  /** at */
  private Boolean at = false;

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
      group = ((GroupMessageEvent) event).getGroup();
      contact = group;
      senderId = ((GroupMemberEvent) event).getMember().getId();
      groupId = contact.getId();
      groupName = group.getName();
      isGroupOperator = isOperator(event);
    }else if (event instanceof MessageEvent){
      content = getTextContent();
      sender = ((MessageEvent) event).getSender();
      senderId = sender.getId();

      if(event instanceof GroupMessageEvent){
        group = ((GroupMessageEvent) event).getGroup();
        contact = group;
        groupId =  contact.getId();
        groupName = group.getName();
        isGroupOperator = isOperator(event);
      }else{
        contact = sender;
      }

      if(event instanceof GroupMessageEvent){
        messageType = 4;
      }else if(event instanceof GroupTempMessageEvent){
        messageType = 3;
      }else if(event instanceof FriendMessageEvent){
        messageType = 2;
      }else if(event instanceof StrangerMessageEvent){
        messageType = 1;
      }
    }

    isRoot = isRootManageQQ(senderId, qqConfig);
    isNormalManage = isManageQQ(senderId, qqConfig, normalManage);
  }

  /**
   * 获取命令
   */
  public String getCommand(){
    String command = null;
    String pattern = "^#(\\S+)(\\s+(.*))?";

    // 创建 Pattern 对象
    Pattern r = Pattern.compile(pattern);

    if(getContent() != null) {
      // 现在创建 matcher 对象
      Matcher m = r.matcher(getContent());
      if (m.find()) {
        command = m.group(1);
      }
    }
    return command;
  }

  /**
   * 获取 命令后的参数
   * @param regex
   * @return
   */
  public String getParameter(String regex){
    if(getContent() != null) {
      return getContent().replaceFirst(regex, "").trim();
    }else{
      return "";
    }
  }

  /**
   * 获取接受的消息体
   * @param prefix
   * @return
   */
  public MessageChainBuilder getMessage(String prefix){
    // 接收的字符串消息
    MessageChainBuilder builder = MessageUtil.createBuilder();
    getMessageEvent().getMessage().forEach(singleMessage -> {
      // 过滤艾特消息
      if (singleMessage instanceof At) {
      } else if(singleMessage instanceof PlainText){
        if(singleMessage.contentToString().trim().startsWith(prefix)){
          builder.append(new PlainText(singleMessage.contentToString().substring(prefix.length()).trim()));
        }else{
          builder.append(singleMessage);
        }
      }else{
        builder.append(singleMessage);
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
    if (event instanceof GroupMessageEvent) {
      getGroupMessageEvent().getMessage().forEach(singleMessage -> {
        // 过滤艾特消息
        if (singleMessage instanceof At) {
          if (((At) singleMessage).getTarget() == getMessageEvent().getBot().getId()) {
            at = true;
          }
        }else {
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
    return strMsg.toString().trim();
  }

  /** 是否是管理员 */
  public boolean isManager(){
    return isNormalManage || isRoot;
  }

  /** 是否可以操作群 */
  public boolean isCanOperatorGroup(){
    return groupId != null && (isGroupOperator || isNormalManage || isRoot);
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

  /**
   * 是否超级管理员
   *
   * @param qq
   * @return
   */
  private boolean isManageQQ(Long qq, QqConfig qqConfig, Set<Long> normalManage) {
    return isRootManageQQ(qq, qqConfig) || normalManage.contains(qq);
  }

  /**
   * 是否群管理员
   *
   * @param event
   * @return
   */
  private boolean isOperator(Event event) {
    MemberPermission permission = null;
    // 群主或者管理员
    if (event instanceof GroupMessageEvent) {
      permission = ((GroupMessageEvent) event).getPermission();
      log.info("-在QQ群-{}-中--{}--发消息--群权限：{}", groupId, senderId, permission);
    }else if (event instanceof  GroupMemberEvent){
      permission = ((GroupMemberEvent) event).getMember().getPermission();
    }

    return permission != null && (MemberPermission.ADMINISTRATOR.equals(permission) || MemberPermission.OWNER.equals(permission));
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
  /**
   * 是否群消息
   * @return
   */
  public boolean isGroupMessage(){
    return messageType == 3 || messageType == 4;
  }
}