package com.shr25.robot.qq.model.Vo;

import lombok.Data;

/**
 * @description: 登录数据
 * @author:: huobing
 * @date: 2022/7/1  12:25
 **/
@Data
public class QqLogin {
    /** 登录需要处理的事件类型 0.登录成功  1.图片验证码  2. 滑动验证码 3.理不安全设备验证*/
    Integer type;

    /** bot的号码 */
    Long qq;

    /** 密码 */
    String password;

    /** 图片验证码 */
    byte[] imgData;

    /** 需要处理的url */
    String url;

    /** 图片验证码的值 */
    String code;

    /** 滑动模块的ticket */
    String ticket;
}
