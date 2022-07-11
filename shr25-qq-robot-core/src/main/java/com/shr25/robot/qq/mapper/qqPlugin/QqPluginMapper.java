package com.shr25.robot.qq.mapper.qqPlugin;

import com.shr25.robot.base.MyBaseMapper;
import com.shr25.robot.qq.model.qqPlugin.QqPlugin;

import java.util.List;

/**
 * QQ群插件Mapper接口
 *
 * @author huobing
 * @date 2022-06-14 14:12:10
 */
public interface QqPluginMapper extends MyBaseMapper<QqPlugin> {
    /**
     * 查询QQ群插件列表
     *
     * @param qqPlugin
     * @return QqPlugin集合
     */
    public List<QqPlugin> selectQqPluginList(QqPlugin qqPlugin);
}