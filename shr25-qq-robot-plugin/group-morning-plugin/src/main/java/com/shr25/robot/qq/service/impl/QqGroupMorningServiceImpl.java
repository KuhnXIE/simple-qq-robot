package com.shr25.robot.qq.service.impl;

import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.mapper.QqGroupMorningMapper;
import com.shr25.robot.qq.model.QqGroupMorning;
import com.shr25.robot.qq.service.IQqGroupMorningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 关键字回复Service业务层处理
 *
 * @author huobing
 * @date 2022-6-28 9:16
 */
@Slf4j
@Service
public class QqGroupMorningServiceImpl extends MyBaseServiceImpl<QqGroupMorningMapper, QqGroupMorning> implements IQqGroupMorningService {
  @Override
  public List<QqGroupMorning> allTask() {
    List<QqGroupMorning> qqGroupMornings = baseMapper.allTask();
    return qqGroupMornings;
  }
}
