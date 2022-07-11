package com.shr25.robot.qq.mapper;

import com.shr25.robot.base.MyBaseMapper;
import com.shr25.robot.qq.model.QqGroupMorning;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * qq群早安信息Mapper接口
 *
 * @author huobing
 * @date 2022-6-28 9:10
 */
public interface QqGroupMorningMapper extends MyBaseMapper<QqGroupMorning> {

  /**
   * 查询所有可以执行的定时任务
   * @return
   */
  @Select("SELECT rqgm.* \n" +
    "FROM\n" +
    "\tr_qq_group_morning rqgm\n" +
    "\tJOIN r_qq_group_plugin rqgp on rqgp.group_id = rqgm.group_id\n" +
    "\tJOIN r_qq_plugin rqp ON rqp.id = rqgp.plugin_id and rqp.state = 1 and rqp.class_name = 'com.shr25.robot.qq.plugins.QQGroupMorningPlugin'")
  List<QqGroupMorning> allTask();
}
