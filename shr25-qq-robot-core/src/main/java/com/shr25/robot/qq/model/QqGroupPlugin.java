package com.shr25.robot.qq.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseCreateTimeEntity;
import lombok.Data;

/**
 * QQ群插件对象 r_qq_group_plugin
 *
 * @author huobing
 * @date 2022-06-14 14:13:04
 */
@Data
@TableName("r_qq_group_plugin")
public class QqGroupPlugin extends BaseCreateTimeEntity {
	/** qq群号 */
	private Long groupId;

	/** 插件id */
	private Long pluginId;

	/** 排序 */
	private Integer sort;

	/** 是否启用 */
	private Integer enabled;
}