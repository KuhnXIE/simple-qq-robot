package com.shr25.robot.dataSources;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * 动态数据源
 * @ClassName: DynamicDataSource
 * @author fuce
 * @date 2019-12-07 18:39
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	public DynamicDataSource(javax.sql.DataSource dataSource, Map<Object, Object> targetDataSources)
    {
        super.setDefaultTargetDataSource(dataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

	@Override
	protected Object determineCurrentLookupKey() {
		
		return DataSourceContextHolder.getDataSource();
	}

}
