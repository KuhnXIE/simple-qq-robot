package com.shr25.robot.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.validation.annotation.Validated;

/**
 * 业务封装基础类
 *
 * @param <M> mapper
 * @param <T> model
 * @author dengqiang
 */
@Validated
public class MyBaseServiceImpl<M extends MyBaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements MyBaseService<T> {

}
