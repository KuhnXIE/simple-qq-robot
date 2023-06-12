package com.shr25.robot.qq.service.happy;

import com.shr25.robot.base.MyBaseService;
import com.shr25.robot.qq.model.happy.SignIns;

/**
* @author XIE
* @description 针对表【sign_ins(签到表)】的数据库操作Service
* @createDate 2023-06-12 14:53:26
*/
public interface SignInsService extends MyBaseService<SignIns> {

    String sign(String nick, Long senderId);
}
