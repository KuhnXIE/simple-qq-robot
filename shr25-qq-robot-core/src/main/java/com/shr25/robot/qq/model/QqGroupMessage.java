package com.shr25.robot.qq.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * QQ群关键消息记录 r_qq_group_message
 *
 * @author huobing
 * @date 2022-6-20 16:30
 */
@Data
@TableName("r_qq_group_message")
public class QqGroupMessage extends BaseEntity {
  /** qq群号 */
  private Long groupId;

  /** qq */
  private Long qq;

  /** qq */
  private String qqName;

  /** 群标签 */
  private String keyword;

  /** qq群名称 */
  private String message;

  /** 创建时间 */
  private Date createTime;

}
