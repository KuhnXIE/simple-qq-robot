package com.shr25.robot.qq.service.qqGroup.impl;

import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.mapper.qqGroup.QqGroupLogMapper;
import com.shr25.robot.qq.model.QqGroupLog;
import com.shr25.robot.qq.service.qqGroup.IQqGroupLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * QQ群进群，退群消息记录Service业务层处理
 *
 * @author huobing
 * @date 2022-6-14 19:47
 */

@Slf4j
@Service
public class QqGroupLogServiceImpl extends MyBaseServiceImpl<QqGroupLogMapper, QqGroupLog> implements IQqGroupLogService {

}
