package com.shr25.robot.qq.plugins;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.model.QqMessage;
import com.shr25.robot.qq.util.MessageUtil;
import kotlin.io.FilesKt;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.RemoteFile;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 媒体插件
 *
 * @author huobing
 * @date 2022/6/3 21:25
 */
@Component
public class MediaRobotPlugin extends RobotPlugin {

  /** 图片文件夹 */
  private File imgFile;

  /** 斗图文件夹 */
  private File stickerFile;

  /** 短视频文件夹 */
  private File shortVideoFile;

  public MediaRobotPlugin() {
    super();
    setName("媒体插件");
    addDesc("看图，看视频，有它就够了");
    addCommand("美图", "生活很苦", true);
    setSort(10000);

    // 媒体文件夹
    File baseMediaPath = FilesKt.resolve(BotConfiguration.getDefault().getWorkingDir(), SpringUtil.getBean(QqConfig.class).getWorkspace() + File.separator + "media" + File.separator);
    this.imgFile = FilesKt.resolve(baseMediaPath, "img");
    this.stickerFile = FilesKt.resolve(baseMediaPath, "sticker");
    this.shortVideoFile = FilesKt.resolve(baseMediaPath, "short_video");
    FileUtil.mkdir(this.imgFile);
    FileUtil.mkdir(this.stickerFile);
    FileUtil.mkdir(this.shortVideoFile);
  }

  @Override
  public boolean executeMessage(QqMessage qqMessage) {
    AtomicBoolean executeNext = new AtomicBoolean(true);
    if (!StringUtil.isBlank(qqMessage.getContent())) {
      switch (qqMessage.getContent()) {
        case "#看图":
          File img = MessageUtil.randomFile(imgFile);
          if (img == null) {
            qqMessage.putReplyMessage("没有图可以看哦~");
          } else {
            qqMessage.putReplyMessage(MessageUtil.buildImageMessage(qqMessage.getContact(), img));
          }
          break;
        case "#斗图":
          File sticker = MessageUtil.randomFile(stickerFile);
          if (sticker == null) {
            qqMessage.putReplyMessage("没有表情包可以看哦~");
          } else {
            qqMessage.putReplyMessage(MessageUtil.buildImageMessage(qqMessage.getContact(), sticker));
          }
          break;
        case "#短视频":

            File shortVideo = MessageUtil.randomFile(shortVideoFile);
            if (shortVideo == null) {
              qqMessage.putReplyMessage("没有短视频可以看哦~");
            } else {
              // 暂时有bug
              Group group = qqMessage.getGroupMessageEvent().getGroup();
              RemoteFile remoteFile = group.getFilesRoot();
              qqMessage.putReplyMessage(group, remoteFile.resolve(shortVideo.getName()).upload(shortVideo));
            }
          break;
        case "美图":
          List<String> list = Arrays.asList(
                  "https://api.yimian.xyz/img",
                  "http://www.dmoe.cc/random.php",
            "http://img.xjh.me/random_img.php",
            "http://img.btu.pp.ua/random/api.php"
          );
          this.sendImagesByUrl(qqMessage, RandomUtil.randomEle(list));
          break;

        default:
          break;
      }
    }
    return executeNext.get();
  }

  /**
   * 根据url发送图片消息
   *
   * @param qqMessage 插件上下文
   * @param url     指向图片的url
   */
  private void sendImagesByUrl(QqMessage qqMessage, String url) {
    HttpResponse response = HttpRequest.get(url).setFollowRedirects(true).execute();
    Image image = MessageUtil.buildImageMessage(qqMessage.getContact(), response.bodyStream());
    if (image != null) {
      qqMessage.putReplyMessage(image);
    }
  }

}
