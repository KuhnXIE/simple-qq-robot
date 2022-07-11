package com.shr25.robot.qq.service.qqGroup.impl;

import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.mapper.qqGroup.QqGroupMessageMapper;
import com.shr25.robot.qq.model.QqGroupMessage;
import com.shr25.robot.qq.service.qqGroup.IQqGroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * QQ群关键消息Service业务层处理
 *
 * @author huobing
 * @date 2022-6-20 16:38
 */
@Slf4j
@Service
public class QqGroupMessageServiceImpl extends MyBaseServiceImpl<QqGroupMessageMapper, QqGroupMessage> implements IQqGroupMessageService {

}
