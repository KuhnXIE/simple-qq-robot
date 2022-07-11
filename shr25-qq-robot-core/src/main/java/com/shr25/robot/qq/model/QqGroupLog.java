package com.shr25.robot.qq.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * QQ群进群，退群消息记录 r_qq_group_log
 *
 * @author huobing
 * @date 2022-6-14 19:47
 */
@Data
@TableName("r_qq_group_log")
public class QqGroupLog extends BaseEntity {
  /** qq群号 */
  private Long groupId;

  /** qq */
  private Long qq;

  /** qq昵称 */
  private String qqName;

  /** 事件，1.邀请入群 2.主动加群 3.踢人 4. 退群 */
  private Integer event;

  /** 邀请人 */
  private Long invite;

  /** 踢人者 */
  private Long kick;

  /** 创建时间 */
  private Date createTime;

}
