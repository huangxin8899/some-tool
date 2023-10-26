package com.huangxin.mybatis.builder;

import com.huangxin.mybatis.ConditionType;
import com.huangxin.mybatis.SqlConstant;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.SerializableFunction;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;

/**
 * UpdateBuilder
 *
 * @author 黄鑫
 */
public class UpdateBuilder extends CommonConditionBuilder<UpdateBuilder> {

    protected final SQL sql = new SQL();
    protected final List<String> setList = new ArrayList<>();

    @Override
    public String build() {
        sql.UPDATE(table);
        setList.forEach(sql::SET);
        whereList.forEach(sql::WHERE);
        orList.forEach(ors -> {
            if (!ors.isEmpty()) {
                sql.OR().WHERE(ors.toArray(new String[0]));
            }
        });
        return sql.toString();
    }

    @Override
    public <R> String getColumn(SerializableFunction<R, ?> function) {
        return FunctionUtil.getMetaColumn(function).wrapColumn();
    }

    public UpdateBuilder update(Class<?> updateClass) {
        return update(AnnoUtil.getTableName(updateClass));
    }

    public UpdateBuilder update(String table) {
        this.table = SqlConstant.wrapBackQuote(table);
        return this;
    }

    public <R> UpdateBuilder set(SerializableFunction<R, ?> function, Object param) {
        return set(true, function, param);
    }

    public <R> UpdateBuilder set(boolean flag, SerializableFunction<R, ?> function, Object param) {
        String columnName = FunctionUtil.getMetaColumn(function).getColumnName();
        return set(flag, columnName, param);
    }

    public UpdateBuilder set(String column, Object param) {
        return set(true, column, param);
    }

    public UpdateBuilder set(boolean flag, String column, Object param) {
        if (flag) {
            String wrapped = SqlConstant.wrapBackQuote(column);
            String resolve = ConditionType.resolve(ConditionType.EQ, wrapped, param, paramMap);
            setList.add(resolve);
        }
        return this;
    }


}
