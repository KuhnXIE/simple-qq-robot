package com.shr25.robot.qq.service;

import com.shr25.robot.base.MyBaseService;
import com.shr25.robot.qq.model.QqGroupMorning;

import java.util.List;

/**
 * 关键字回复Service接口
 *
 * @author huobing
 * @date 2022-6-28 9:12
 */
public interface IQqGroupMorningService extends MyBaseService<QqGroupMorning> {

  /**
   * 获取全部可以执行的配置信息
   * @return
   */
  List<QqGroupMorning> allTask();
}
