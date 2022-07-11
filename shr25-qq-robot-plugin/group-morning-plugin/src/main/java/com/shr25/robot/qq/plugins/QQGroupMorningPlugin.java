package com.shr25.robot.qq.plugins;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.model.msg.MsgContent;
import com.shr25.robot.qq.model.QqGroupMorning;
import com.shr25.robot.qq.service.IQqGroupMorningService;
import com.shr25.robot.qq.service.msg.IMsgContentService;
import com.shr25.robot.qq.util.MessageUtil;
import com.shr25.robot.utils.DateUtils;
import kotlin.io.FilesKt;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 早安定时器
 *
 * @author huobing
 * @date 2022-6-27 21:44
 */
@Component
public class QQGroupMorningPlugin extends RobotPlugin {
  @Autowired
  IQqGroupMorningService qqGroupMorningService;

  @Autowired
  IMsgContentService msgContentService;

  @Value("${shr25.plugin.morning.defaultMorningDate:07:30}")
  private String defaultMorningDate;

  private File morningImgDir;

  public QQGroupMorningPlugin() {
    super();
    setName("定时早安插件");
    addDesc("定时执行早安问候,提醒群员签到");
    addCommand("#获取早安时间","获取设置的定时早安时间", true);
    addCommand("#设置早安时间 {HH:mm}","设置定时早安时间 如：07:10", true);
    setSort(10002);
  }

  @Override
  public boolean executeGroupMessage(QqMessage qqMessage) {
    String command = qqMessage.getCommand();
    if(command != null){
      switch (command){
        case "获取早安时间":
          QqGroupMorning old = qqGroupMorningService.query().eq("group_id", qqMessage.getGroupId()).one();
          qqMessage.putReplyMessage("早安时间："+ old.getSendDate());
          break;
        case "设置早安时间":
          String sendDate = qqMessage.getParameter("#"+command).replace("：", ":");
          if(sendDate.matches("[012]\\d:[0-6]\\d")){
            Date sendTime = DateUtils.getDateFromString(sendDate);
            if(sendTime.getTime() > DateUtils.addDay(DateUtils.getDayStartTime(), 1).getTime()){
              qqMessage.putReplyMessage("时间范围不对：" + sendDate);
            }else {

              if (sendTime.getTime() <= System.currentTimeMillis()) {
                //如果已经过时间了，则加一天
                sendTime = DateUtils.addDay(sendTime, 1);
              }
              QqGroupMorning qqGroupMorning = qqGroupMorningService.query().eq("group_id", qqMessage.getGroupId()).one();
              if (qqGroupMorning == null) {
                qqGroupMorning = new QqGroupMorning();
                qqGroupMorning.setGroupId(qqMessage.getGroupId());
                qqGroupMorning.setSendDate(sendDate);
                qqGroupMorning.setSendTime(sendTime);
              } else {
                qqGroupMorning.setSendDate(sendDate);
                qqGroupMorning.setSendTime(sendTime);
              }

              if (qqGroupMorning.getId() == null) {
                qqGroupMorningService.save(qqGroupMorning);
              } else {
                qqGroupMorningService.update().set("send_date", sendDate).set("send_time", qqGroupMorning.getSendTime()).eq("id", qqGroupMorning.getId()).update();
              }

              addDelayQueue(qqGroupMorning.getGroupId(), qqGroupMorning.getSendTime());
              qqMessage.putReplyMessage("设置早安时间成功：" + sendDate);
            }
          }else{
            qqMessage.putReplyMessage("时间格式不正确：" + sendDate);
          }
          break;
      }
    }
    return true;
  }

  @PostConstruct
  public void init(){
    // 媒体文件夹
    File baseMediaPath = FilesKt.resolve(BotConfiguration.getDefault().getWorkingDir(), SpringUtil.getBean(QqConfig.class).getWorkspace() + File.separator + "media" + File.separator);
    this.morningImgDir = FilesKt.resolve(baseMediaPath, "morning_img");
    FileUtil.mkdir(this.morningImgDir);

    List<QqGroupMorning> allTasks = qqGroupMorningService.allTask();
    if(allTasks != null){
      allTasks.forEach(item -> {
        if(item.getSendTime().getTime() > System.currentTimeMillis()){
          addDelayQueue(item.getGroupId(), item.getSendTime());
        }else{
          //如果已经过时间了，则加一天
          Date sendTime = DateUtils.getDateFromString(item.getSendDate());
          sendTime = DateUtils.addDay(sendTime, 1);
          item.setSendTime(sendTime);
          qqGroupMorningService.update().set("send_time", sendTime).eq("id", item.getId()).update();
          addDelayQueue(item.getGroupId(), item.getSendTime());
        }
      });
    }
  }

  @Override
  protected void task(Long groupId, Object data) {
    QqGroupMorning qqGroupMorning = qqGroupMorningService.query().eq("group_id", groupId).one();
    Group group = MessageUtil.getGroup(groupId);
    if(qqGroupMorning != null && group != null) {
      MessageChainBuilder builder = MessageUtil.createBuilder();
      if(qqGroupMorning.getIsMsg() == 1) {
        if (qqGroupMorning.getIsRandomImg() == 1) {
          File file = MessageUtil.randomFile(this.morningImgDir);

          if (file != null) {
            builder.append(MessageUtil.buildImageMessage(group, file));
          }
        } else if (qqGroupMorning.getImg() != null) {
          File file = MessageUtil.getFile(this.morningImgDir, qqGroupMorning.getImg());
          if (file != null) {
            builder.append(MessageUtil.buildImageMessage(group, file));
          }
        }
      }
      if(qqGroupMorning.getIsMsg() == 1){
        if(qqGroupMorning.getIsRandomMsg() == 1){
          MsgContent msgContent = msgContentService.getRandomMsg("早安");

          if(msgContent != null){
            builder.append(msgContent.getContent());
          }
        }else if(qqGroupMorning.getMsg() != null){
          builder.append("\n"+qqGroupMorning.getMsg());
        }
      }

      MessageUtil.sendGroupMessage(groupId, builder.build());

      if (qqGroupMorning.getIsSignIn() == 1) {
        MessageUtil.sendGroupMessage(groupId, "签到");
      }

      //如果已经过时间了，则加一天
      Date sendTime = DateUtils.getDateFromString(qqGroupMorning.getSendDate());
      sendTime = DateUtils.addDay(sendTime, 1);
      qqGroupMorningService.update().set("send_time", sendTime).eq("id", qqGroupMorning.getId()).update();
      addDelayQueue(qqGroupMorning.getGroupId(), sendTime);
    }
  }

  @Override
  public void init(Long groupId) {
    log.info("{}===》添加插件==》{}", groupId, getName());
    QqGroupMorning qqGroupMorning = qqGroupMorningService.query().eq("group_id", groupId).one();
    if(qqGroupMorning == null){
      log.info("{}===》添加插件==》{}===>没有配置过，新建配置！~~~~~~~~", groupId, getName());
      qqGroupMorning = new QqGroupMorning();
      qqGroupMorning.setGroupId(groupId);
      qqGroupMorning.setSendDate(defaultMorningDate);
      //如果已经过时间了，则加一天
      Date sendTime = DateUtils.getDateFromString(qqGroupMorning.getSendDate());
      qqGroupMorning.setSendTime(sendTime);
    }

    log.info("{}===》添加插件==》{}===>判断设置的默认“{}”时间是否大于当前时间！~~~~~~~~", groupId, getName(), qqGroupMorning.getSendDate());
    if(qqGroupMorning.getSendTime().getTime() <= System.currentTimeMillis()){
      log.info("{}===》添加插件==》{}===>设置的时间小于于当前时间，需要设置到第二天！~~~~~~~~", groupId, getName());
      //如果已经过时间了，则加一天
      Date sendTime = DateUtils.getDateFromString(qqGroupMorning.getSendDate());
      sendTime = DateUtils.addDay(sendTime, 1);
      qqGroupMorning.setSendTime(sendTime);
    }

    if(qqGroupMorning.getId() == null){
      log.info("{}===》添加插件==》{}===>保存设置！~~~~~~~~", groupId, getName());
      qqGroupMorningService.save(qqGroupMorning);
    }else{
      log.info("{}===》添加插件==》{}===>修改设置！~~~~~~~~", groupId, getName());
      qqGroupMorningService.update().set("send_time", qqGroupMorning.getSendTime()).eq("id", qqGroupMorning.getId()).update();
    }

    addDelayQueue(qqGroupMorning.getGroupId(), qqGroupMorning.getSendTime());
  }

  @Override
  public void cancel(Long groupId) {
    super.cancel(groupId);
  }
}
