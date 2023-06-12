package com.shr25.robot.qq.model.happy;

import java.util.Date;

import com.shr25.robot.base.BaseEntity;
import lombok.Data;

/**
* 签到表
* @TableName sign_ins
*/
@Data
public class SignIns extends BaseEntity {

    /**
    * 用户ID，用于关联用户表
    */
    private Long userId;
    /**
    * 签到时间
    */
    private Date signInTime;
    /**
    * 备注，可选字段，记录额外信息
    */
    private String note;

}
