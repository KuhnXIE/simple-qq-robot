package com.shr25.robot.qq.plugins;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqGroupInfo;
import com.shr25.robot.qq.model.QqGroupLog;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.service.qqGroup.IQqGroupInfoService;
import com.shr25.robot.qq.service.qqGroup.IQqGroupLogService;
import com.shr25.robot.qq.util.MessageUtil;
import kotlin.io.FilesKt;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 群管理插件
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Component
public class GroupManageRobotPlugin extends RobotPlugin {
    @Autowired
    IQqGroupLogService qqGroupLogService;
    @Autowired
    IQqGroupInfoService qqGroupInfoService;

    private File welcomeImgDir;

    public GroupManageRobotPlugin() {
        super();
        log.info("开始加载“群管理插件V2.0”");
        setName("群管理插件");
        addDesc("自动欢迎入群成员,记录群员进出情况");
        addCommand("开启迎新", "开启新用户进群迎新消息", qqMessage -> {
            enabledWelcome(qqMessage);
            return true;
        },  true);
        addCommand("关闭迎新", "关闭新用户进群迎新消息", qqMessage -> {
            notEnabledWelcome(qqMessage);
            return true;
        }, true);
        addCommand("开启迎新图片", "开启新用户进群随机欢迎图片", qqMessage -> {
            enabledWelcomeImg(qqMessage);
            return true;
        });
        addCommand("关闭迎新图片", "关闭新用户进群随机欢迎图片", qqMessage -> {
            notEnabledWelcomeImg(qqMessage);
            return true;
        });
        addCommand("开启迎新信息", "开启新用户进群欢迎语", qqMessage -> {
            enabledWelcomeMsg(qqMessage);
            return true;
        });
        addCommand("关闭迎新信息", "关闭新用户进群欢迎语", qqMessage -> {
            notEnabledWelcomeMsg(qqMessage);
            return true;
        });
        addCommand("设置迎新信息", "设置新用户进群欢迎语", qqMessage -> {
            MessageChainBuilder messageChainBuilder = qqMessage.getMessage();
            setWelcome(qqMessage, MiraiCode.serializeToMiraiCode(messageChainBuilder));
            return true;
        });
        setSort(1000);
        // 媒体文件夹
        File baseMediaPath = FilesKt.resolve(BotConfiguration.getDefault().getWorkingDir(), SpringUtil.getBean(QqConfig.class).getWorkspace() + File.separator + "media" + File.separator);
        this.welcomeImgDir = FilesKt.resolve(baseMediaPath, "welcome_img");
        FileUtil.mkdir(this.welcomeImgDir);
    }

    /**
     * 开启开启迎新
     * @param qqMessage
     */
    private void enabledWelcome(QqMessage qqMessage){
        initGroup(qqMessage);
        qqGroupInfoService.update().set("is_img", 1).set("is_msg", 1).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }

    /**
     * 关闭迎新
     * @param qqMessage
     */
    private void notEnabledWelcome(QqMessage qqMessage){
        qqGroupInfoService.update().set("is_img", 0).set("is_msg", 0).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }

    /**
     * 开启迎新随机图片
     * @param qqMessage
     */
    private void enabledWelcomeImg(QqMessage qqMessage){
        initGroup(qqMessage);
        qqGroupInfoService.update().set("is_img", 1).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }
    /**
     * 关闭迎新随机图片
     * @param qqMessage
     */
    private void notEnabledWelcomeImg(QqMessage qqMessage){
        qqGroupInfoService.update().set("is_img", 0).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }

    /**
     * 开启迎新欢迎语
     * @param qqMessage
     */
    private void enabledWelcomeMsg(QqMessage qqMessage){
        initGroup(qqMessage);
        qqGroupInfoService.update().set("is_msg", 1).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }
    /**
     * 关闭迎新欢迎语
     * @param qqMessage
     */
    private void notEnabledWelcomeMsg(QqMessage qqMessage){
        qqGroupInfoService.update().set("is_msg", 0).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }

    /**
     * 设置迎新欢迎语
     * @param qqMessage
     * @param welcome
     */
    private void setWelcome(QqMessage qqMessage, String welcome){
        initGroup(qqMessage);
        qqGroupInfoService.update().set("is_msg", 1).set("welcome", welcome).eq("group_id", qqMessage.getGroupId()).update();
        qqMessage.putReplyMessage("设置成功！~~~~");
    }

    private void initGroup(QqMessage qqMessage){
        QqGroupInfo qqGroupInfo = qqGroupInfoService.query().eq("group_id", qqMessage.getGroupId()).one();
        if(qqGroupInfo == null){
            Group group = MessageUtil.getGroup(qqMessage.getGroupId());
            qqGroupInfo = new QqGroupInfo();
            qqGroupInfo.setGroupId(group.getId());
            qqGroupInfo.setGroupName(group.getName());
            qqGroupInfoService.save(qqGroupInfo);
        }
    }

    @Override
    public boolean executeGroupMember(QqMessage qqMessage) {
        //入群或出群的qq
        Group group = qqMessage.getGroupMemberEvent().getGroup();
        Member member = qqMessage.getGroupMemberEvent().getMember();
        AtomicBoolean executeNext = new AtomicBoolean(true);
        QqGroupLog qqGroupLog = new QqGroupLog();
        qqGroupLog.setGroupId(group.getId());
        qqGroupLog.setQq(member.getId());
        qqGroupLog.setQqName(member.getNick());
        if (qqMessage.getEvent() instanceof MemberJoinEvent.Invite) {
            welcome(qqMessage);
            qqGroupLog.setEvent(1);
            qqGroupLog.setInvite(qqMessage.getInviteMemberJoinEvent().getInvitor().getId());
        } else if (qqMessage.getEvent() instanceof MemberJoinEvent.Active) {
            welcome(qqMessage);

            qqGroupLog.setEvent(2);
        } else if (qqMessage.getEvent() instanceof MemberLeaveEvent.Kick) {
            MemberLeaveEvent.Kick event = qqMessage.getKickMemberLeaveEvent();
            qqGroupLog.setEvent(3);
            if(event.getOperator() != null){
                qqGroupLog.setKick(event.getOperator().getId());
            }
        } else if (qqMessage.getEvent() instanceof MemberLeaveEvent.Quit) {
            out(qqMessage);
            qqGroupLog.setEvent(4);
        }
        qqGroupLog.setCreateTime(new Date());
        qqGroupLogService.save(qqGroupLog);
        return executeNext.get();
    }

    /**
     * 欢迎语
     * @param qqMessage
     */
    private void welcome(QqMessage qqMessage){
        Member member = qqMessage.getGroupMemberEvent().getMember();
        QqGroupInfo qqGroupInfo = qqGroupInfoService.query().eq("group_id", qqMessage.getGroupId()).one();

        if(qqGroupInfo !=  null && (qqGroupInfo.getIsImg()==1 || qqGroupInfo.getIsMsg() == 1)){
            MessageChainBuilder builder = MessageUtil.createBuilder();
            builder.append(new At(member.getId()));
            if(qqGroupInfo.getIsImg()==1){
                File file = MessageUtil.randomFile(this.welcomeImgDir);
                if(file != null) {
                    if (file != null) {
                        builder.append(MessageUtil.buildImageMessage(qqMessage.getContact(), file));
                    }
                }
            }
            if(qqGroupInfo.getIsMsg() == 1 && StringUtils.isNotBlank(qqGroupInfo.getWelcome())){
                MessageChain messageChain = MiraiCode.deserializeMiraiCode(qqGroupInfo.getWelcome());
                builder.addAll(messageChain);
            }
            qqMessage.putReplyMessage(qqMessage.getContact(), builder.build());
        }
    }

    /**
     * 退出发送提醒
     * @param qqMessage
     */
    private void out(QqMessage qqMessage){
        QqGroupInfo qqGroupInfo = qqGroupInfoService.query().eq("group_id", qqMessage.getGroupId()).one();
        if(qqGroupInfo != null && qqGroupInfo.getIsOutMessage() == 1) {
            // 发一条消息到群里
            MemberLeaveEvent.Quit event = qqMessage.getQuitMemberLeaveEvent();
            // 构建链式消息
            At at = new At(event.getGroup().getOwner().getId());
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.append(at);
            messageChainBuilder.append(String.format("\n有人离开了：%s(%s)", event.getMember().getNick(), event.getMember().getId()));
            qqMessage.putReplyMessage(event.getGroup(), messageChainBuilder);
        }
    }
}
