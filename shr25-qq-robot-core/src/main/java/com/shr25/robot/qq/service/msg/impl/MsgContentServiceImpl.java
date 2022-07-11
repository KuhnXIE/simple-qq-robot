package com.shr25.robot.qq.service.msg.impl;

import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.mapper.msg.MsgContentMapper;
import com.shr25.robot.qq.model.msg.MsgContent;
import com.shr25.robot.qq.service.msg.IMsgContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
  * qq群早安信息Service业务层处理
  *
  * @author huobing
  * @date 2022-6-26 20:45
  */
@Slf4j
@Service
public class MsgContentServiceImpl extends MyBaseServiceImpl<MsgContentMapper, MsgContent> implements IMsgContentService {
  private List<String> keywords;

  @PostConstruct
  public void init(){
    keywords = query().select("keyword").groupBy("keyword").list().stream().map(item -> {return item.getKeyword();}).collect(Collectors.toList());
  }

  @Override
  public MsgContent getRandomMsg(String keyword) {
    if(keywords.contains(keyword)){
      Integer count = query().eq("keyword", keyword).count();
      Integer randomCount = (int)(Math.random()*count);
      MsgContent msgContent = query().eq("keyword", keyword).last("limit "+ randomCount + ",1").one();
      return msgContent;
    }
    return null;
  }
}
