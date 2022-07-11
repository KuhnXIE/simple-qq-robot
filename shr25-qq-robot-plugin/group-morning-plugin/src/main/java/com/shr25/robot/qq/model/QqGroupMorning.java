package com.shr25.robot.qq.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * qq群早安信息 r_qq_group_morning
 *
 * @author huobing
 * @date 2022-6-25 10:56
 */
@Data
@TableName("r_qq_group_morning")
public class QqGroupMorning extends BaseEntity {

  /** qq群号 */
  private Long groupId;

  /** 固定图片 */
  private String img;

  /** 早安语 */
  private String msg;

  /** 是否发送图片 */
  private Integer isImg;

  /** 是否发送早安语 0 否 1是 */
  private Integer isMsg;

  /** 是否随机发送图片 */
  private Integer isRandomImg;

  /** 是否随机发送早安语 0 否 1是 */
  private Integer isRandomMsg;

  /** 是否提醒签到 0 否 1是 */
  private Integer isSignIn;

  /** 下一次发送的时间 */
  private Date sendTime;

  /** 提醒时间，hh:MM */
  private String sendDate;

  /** 创建时间 */
  private Date createTime;

}
