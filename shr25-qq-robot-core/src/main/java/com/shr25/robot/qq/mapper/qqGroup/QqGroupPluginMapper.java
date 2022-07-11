package com.shr25.robot.qq.mapper.qqGroup;

import com.shr25.robot.base.MyBaseMapper;
import com.shr25.robot.qq.model.QqGroupPlugin;

import java.util.List;

/**
 * QQ群插件Mapper接口
 *
 * @author huobing
 * @date 2022-06-14 14:13:04
 */
public interface QqGroupPluginMapper extends MyBaseMapper<QqGroupPlugin> {
                                                                                                                            
    /**
     * 查询QQ群插件列表
     *
     * @param qqGroupPlugin
     * @return QqGroupPlugin集合
     */
    public List<QqGroupPlugin> selectQqGroupPluginList(QqGroupPlugin qqGroupPlugin);
}