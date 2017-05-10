package com.magic.user.utils;

import com.magic.user.enums.IEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 20:20
 */
public class EnumKeyTypeHandler<E extends Enum<E> & IEnum<E>> extends
        BaseTypeHandler<IEnum> {

    private Class<IEnum> type;

    public EnumKeyTypeHandler(Class<IEnum> type) {
        if (type == null)
            throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    private IEnum convert(int status) {
        IEnum[] objs = type.getEnumConstants();
        for (IEnum em : objs) {
            if (em.value() == status) {
                return em;
            }
        }
        return null;
    }

    @Override
    public IEnum getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return convert(rs.getInt(columnName));
    }

    @Override
    public IEnum getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return convert(rs.getInt(columnIndex));
    }

    @Override
    public IEnum getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return convert(cs.getInt(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    IEnum enumObj, JdbcType jdbcType) throws SQLException {
        // baseTypeHandler已经帮我们做了parameter的null判断
        ps.setInt(i, enumObj.value());

    }
}
