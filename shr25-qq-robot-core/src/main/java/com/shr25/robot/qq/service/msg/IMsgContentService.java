package com.shr25.robot.qq.service.msg;

import com.shr25.robot.base.MyBaseService;
import com.shr25.robot.qq.model.msg.MsgContent;

/**
 * qq群早安信息Service接口
 *
 * @author huobing
 * @date 2022-6-26 20:42
 */
public interface IMsgContentService extends MyBaseService<MsgContent> {

  /**
   * 获取随机消息
   * @param keyword
   * @return
   */
  MsgContent getRandomMsg(String keyword);
}
