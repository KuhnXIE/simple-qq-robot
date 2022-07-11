package com.shr25.robot.qq.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * QQ群信息 r_qq_group_info
 *
 * @author huobing
 * @date 2022-6-20 16:30
 */
@Data
@TableName("r_qq_group_info")
public class QqGroupInfo extends BaseEntity {
  /** qq群号 */
  private Long groupId;

  /** qq群名称 */
  private String groupName;

  /** 群标签 */
  private String keyword;

  /** 是否发送图片 */
  private Integer isImg;

  /** 是否发送欢迎语 0 否 1是 */
  private Integer isMsg;

  /** 欢迎语 */
  private String welcome;

  /** 是否退出提醒群主 0 否 1是 */
  private Integer isOutMessage;

  /** 创建时间 */
  private Date createTime;

}
