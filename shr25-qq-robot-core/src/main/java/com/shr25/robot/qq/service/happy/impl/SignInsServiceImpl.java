package com.shr25.robot.qq.service.happy.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.model.Users;
import com.shr25.robot.qq.model.happy.SignIns;
import com.shr25.robot.qq.service.UsersService;
import com.shr25.robot.qq.service.happy.SignInsService;
import com.shr25.robot.qq.model.mapper.SignInsMapper;
import com.shr25.robot.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
* @author XIE
* @description 针对表【sign_ins(签到表)】的数据库操作Service实现
* @createDate 2023-06-12 14:53:26
*/
@Service
public class SignInsServiceImpl extends MyBaseServiceImpl<SignInsMapper, SignIns>
    implements SignInsService{

    @Autowired
    private UsersService usersService;

    @Override
    public String sign(String nick, Long senderId) {
        Users users = usersService.getByAccount(String.valueOf(senderId));
        if (users == null){
            users = new Users();
            users.setName(nick);
            users.setDiamond(0);
            users.setCoupon(0);
            users.setAccount(String.valueOf(senderId));
            usersService.save(users);
        }

        LocalDateTime date = DateUtils.getZero();

        SignIns todaySign = getOne(Wrappers.<SignIns>lambdaQuery().ge(SignIns::getSignInTime, date).eq(SignIns::getUserId, users.getId()));

        int count = count(Wrappers.<SignIns>lambdaQuery().ge(SignIns::getSignInTime, date));

        if (todaySign == null){
            users.setDiamond(users.getDiamond() + 1000);

            todaySign = new SignIns();
            todaySign.setUserId(users.getId());
            todaySign.setNote("———签到奖励———\n" +
                    "昵称："+ nick +"\n" +
                    "排名：第"+ (count + 1) +"名\n" +
                    "奖励：1000钻石\n" +
                    "时间："+ DateUtils.dateFormat(Calendar.getInstance(), "HH:mm:ss") +"\n" +
                    "♡\uD835\uDD53\uD835\uDD56 \uD835\uDD59\uD835\uDD52\uD835\uDD61\uD835\uDD61\uD835\uDD6A ➹♡\n" +
                    "      ᵕ̈    ᵕ̈  \uD83D\uDD06  ᵕ̈  \n" +
                    "✿有事做   有人爱    有所期待✿");
            save(todaySign);
            return todaySign.getNote();
        }else {
            return "你已签到，签到时间:" + DateUtils.dateFormat(todaySign.getSignInTime(), "HH:mm:ss");
        }
    }
}




