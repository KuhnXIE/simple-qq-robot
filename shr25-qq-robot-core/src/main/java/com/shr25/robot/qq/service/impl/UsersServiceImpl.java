package com.shr25.robot.qq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shr25.robot.base.MyBaseServiceImpl;
import com.shr25.robot.qq.model.Users;
import com.shr25.robot.qq.service.UsersService;
import com.shr25.robot.qq.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author XIE
* @description 针对表【users(用户表)】的数据库操作Service实现
* @createDate 2023-06-12 14:46:53
*/
@Service
public class UsersServiceImpl extends MyBaseServiceImpl<UsersMapper, Users>
    implements UsersService{


    @Override
    public Users getByAccount(String account) {
        LambdaQueryWrapper<Users> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(Users::getAccount, account);
        return getOne(lambdaQuery);
    }
}




