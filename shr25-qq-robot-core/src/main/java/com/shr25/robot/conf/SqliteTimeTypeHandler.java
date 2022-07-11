package com.shr25.robot.conf;

import com.shr25.robot.utils.DateUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @description: TODO
 * @author:: huobing
 * @date: 2022-7-9 23:34
 **/
public class SqliteTimeTypeHandler extends BaseTypeHandler<Date> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, DateUtils.dateFormat(parameter));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value != null) {
            return DateUtils.getDateFromString(value);
        } else {
            return null;
        }
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value != null) {
            return DateUtils.getDateFromString(value);
        } else {
            return null;
        }
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value != null) {
            return DateUtils.getDateFromString(value);
        } else {
            return null;
        }
    }
}
