package com.shr25.robot.qq.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 消息处理工具
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Slf4j
public class MessageUtil {

    /**
     * 创建一个消息构造器
     *
     * @return {@link MessageChainBuilder}
     */
    public static MessageChainBuilder createBuilder() {
        return new MessageChainBuilder();
    }

    /**
     * 构建图片消息
     *
     * @param sender Group Or Friend Or Member 对象
     * @param image  图片文件
     * @return 构建的图片消息
     */
    public static Image buildImageMessage(Contact sender, File image) {
        ExternalResource externalImage = null;
        try {
            externalImage = ExternalResource.create(image);
            return sender.uploadImage(externalImage);
        } catch (Exception e) {
            log.error("文件消息转换失败", e);
        } finally {
            if (externalImage != null) {
                try {
                    externalImage.close();
                } catch (IOException e) {
                    log.error("externalImage关闭失败", e);
                }
            }
        }
        return null;
    }

    /**
     * 构建图片消息
     *
     * @param sender Group Or Friend Or Member 对象
     * @param image  图片文件
     * @return 构建的图片消息
     */
    public static Image buildImageMessage(Contact sender, InputStream image) {
        ExternalResource externalImage = null;
        try {
            externalImage = ExternalResource.create(image);
            return sender.uploadImage(externalImage);
        } catch (Exception e) {
            log.error("文件消息转换失败", e);
        } finally {
            if (externalImage != null) {
                try {
                    externalImage.close();
                } catch (IOException e) {
                    log.error("externalImage关闭失败", e);
                }
            }
        }
        return null;
    }

    /**
     * 获取一个随机的文件，自动判断是否在文件夹
     *
     * @param file 需要获取的文件夹
     * @return 获取到的文件，可能为null
     */
    public static File randomFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                return file;
            } else if (file.isDirectory()) {
                File[] imgList = file.listFiles();
                if (imgList != null && imgList.length > 0) {
                    return randomFile(RandomUtil.randomEle(imgList));
                }
            }
        }
        return null;
    }

    /**
     * 获取指定的图片
     * @param dir
     * @param img
     * @return
     */
    public static File getFile(File dir, String img) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File imgFile = new File(dir.getAbsolutePath()+File.separator+img);
                if (imgFile.exists()) {
                    return imgFile;
                }
            }
        }
        return null;
    }

    /**
     * 直接发送群消息
     * @param groupId
     * @param message
     */
    public static void sendGroupMessage(Long groupId, String message){
        sendGroupMessage(groupId, new PlainText(message));
    }

    /**
     * 直接发送群消息
     * @param groupId
     * @param message
     */
    public static void sendGroupMessage(Long groupId, Message message){
        Group group = getGroup(groupId);
        if(group != null){
            group.sendMessage(message);
        }
    }

    /**
     * 获取QQ群
     */
    public static ContactList<Group> getAllGroups(){
        List<Group> groups = new ArrayList<>();
        Bot.getInstances().forEach(item -> {
            for (Group group : item.getGroups()) {
                if(!(groups.contains(group.getId()))){
                    groups.add(group);
                }
            }
        });
        return new ContactList(groups);
    }

    /**
     * 获取QQ群
     * @param groupId
     */
    public static Group getGroup(Long groupId){
        Group group = null;
        for (Bot bot : Bot.getInstances()) {
            group = bot.getGroup(groupId);
            if(bot != null){
                break;
            }
        }

        return group;
    }

    /**
     * 获取QQ群用户
     * @param groupId
     * @param qq
     */
    public static Member getGroupMember(Long groupId, Long qq){
        Group group = getGroup(groupId);
        if(group != null){
            return group.get(qq);
        }
        return null;
    }

    /**
     * 获取QQ群用户
     * @param groupId
     * @param nick
     */
    public static Member getGroupMember(Long groupId, String nick){
        AtomicReference<Member> member = new AtomicReference<>();
        Group group = getGroup(groupId);
        if(group != null){
            group.getMembers().forEach(normalMember -> {
                if(normalMember.getNick().equals(nick)){
                    member.set(normalMember);
                }
            });
        }
        return member.get();
    }

    /**
     * 获取QQ群用户
     * @param group
     */
    public static Member getGroupMember(Group group, Long qq){
        if(group != null){
            return group.get(qq);
        }
        return null;
    }

    /**
     * 获取QQ群用户
     * @param group
     * @param nick
     */
    public static Member getGroupMember(Group group, String nick){
        AtomicReference<Member> member = new AtomicReference<>();
        if(group != null){
            group.getMembers().forEach(normalMember -> {
                if(normalMember.getNick().equals(nick)){
                    member.set(normalMember);
                }
            });
        }
        return member.get();
    }

    /**
     * 获取好友
     * @param qq
     */
    public static Friend getFriend(Long qq){
        return Bot.getInstances().get(0).getFriend(qq);
    }

    /**
     * 获取陌生人
     * @param qq
     */
    public static Stranger getStranger(Long qq){
        return Bot.getInstances().get(0).getStranger(qq);
    }
}
