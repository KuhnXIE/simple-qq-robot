package com.shr25.robot.qq.model.qqPlugin;


import com.baomidou.mybatisplus.annotation.TableName;
import com.shr25.robot.base.BaseEntity;
import lombok.Data;

/**
 * QQ群插件对象 r_qq_plugin
 *
 * @author huobing
 * @date 2022-06-14 14:12:10
 */
@Data
@TableName("r_qq_plugin")
public class QqPlugin extends BaseEntity {
	/** 插件名称 */
	private String name;

	/** 插件描述 */
	private String pluginDesc;

	/** 状态， 1启动  0关闭 */
	private Integer state;

	/** 默认排序 */
	private Integer sort;

	/** 类的完整名称 */
	private String className;

	/** 是否所有都可以使用 */
	private Integer isAll;

}