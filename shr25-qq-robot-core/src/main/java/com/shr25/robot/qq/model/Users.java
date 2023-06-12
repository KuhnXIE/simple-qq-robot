package com.shr25.robot.qq.model;

import com.baomidou.mybatisplus.annotation.TableName;

import com.shr25.robot.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users extends BaseEntity {

    private String name;

    private String account;

    private Integer diamond;

    private Integer coupon;

    private Integer isDel;

    private Date createTime;

    private Date updateTime;

}