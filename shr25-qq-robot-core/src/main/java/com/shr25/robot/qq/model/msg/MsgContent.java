package com.shr25.robot.qq.model.msg;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 关键字回复
 *
 * @author huobing
 * @date 2022-6-25 10:50
 */
@Data
@TableName("r_msg_content")
public class MsgContent  extends BaseEntity {
  /** 关键字 */
  private String keyword;

  /** 回复消息 */
  private String content;

  /** 创建时间 */
  private Date createTime;
}
