package com.shr25.robot.qq.service;

import com.shr25.robot.base.MyBaseService;
import com.shr25.robot.qq.model.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author XIE
* @description 针对表【users(用户表)】的数据库操作Service
* @createDate 2023-06-12 14:46:53
*/
public interface UsersService extends MyBaseService<Users> {

    Users getByAccount(String account);
}
