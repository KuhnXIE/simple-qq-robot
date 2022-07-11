package com.shr25.robot.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author huobing
 * @Date 2021/1/25 9:02
 * @Version 1.0.0
 **/
public interface MyBaseMapper<T> extends BaseMapper<T> {
	int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

	/**
	 * 批量插入
	 * @param tableName 表名
	 * @param keys      字段对应属性名称: {"字段名称":"属性名称"}
	 * @param list      插入的数据实体对象集
	 * @return 受影响行数
	 */
	@Insert("<script>insert into ${tableName}"
		+ " <foreach collection=\"keys.entrySet()\" index=\"key\" open=\"(\" close=\")\" separator=\",\" >"
		+ "     ${key}"
		+ " </foreach>"
		+ " values"
		+ " <foreach collection=\"list\" item=\"item\" separator=\",\">"
		+ "     <foreach collection=\"keys.entrySet()\" index=\"key\" item=\"value\" open=\"(\" close=\")\" separator=\",\">"
		+ "         #{item.${value}}"
		+ "     </foreach>"
		+ " </foreach>"
		+ "</script>")
	int saveBatchSplice(@Param("tableName") String tableName, @Param("keys") Map<String,String> keys, @Param("list") List<T> list);

	/**
	 * 批量插入
	 * @param tableName 表名
	 * @param keys      字段对应属性名称: {"字段名称":"属性名称"}
	 * @param list      插入的数据实体对象集
	 * @return 受影响行数
	 */
	@Update("<script><foreach collection=\"list\" item=\"item\" index=\"index\" separator=\";\">"
		+ "<if test=\"item != null\">"
		+ "            update ${tableName}"
		+ "            <set>"
		+ "                <foreach collection=\"keys.entrySet()\" index=\"key\" item=\"value\">"
		+ "                     <if test=\"item[value] != null\">"
		+ "                         ${key} = #{item.${value}},"
		+ "                     </if>"
		+ "                </foreach>"
		+ "            </set>"
		+ "            where id = #{item.id}"
		+ "</if>"
		+ "</foreach>"
		+ "</script>")
	int updateBatchSplice(@Param("tableName") String tableName, @Param("keys") Map<String,String> keys, @Param("list") List<T> list);
}
