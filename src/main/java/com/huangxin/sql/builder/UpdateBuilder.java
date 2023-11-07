package com.huangxin.sql.builder;

import cn.hutool.core.util.ObjectUtil;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.type.ConditionType;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.util.BuilderUtil;
import com.huangxin.sql.util.SqlSessionUtil;
import com.huangxin.sql.util.AnnoUtil;
import com.huangxin.sql.util.FunctionUtil;
import com.huangxin.sql.func.SerializableFunction;
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
    private boolean allowNull = BuilderConfig.ALLOW_NULL;

    @Override
    public String build() {
        sql.UPDATE(table);
        setList.forEach(sql::SET);
        whereList.forEach(sql::WHERE);
        orNestList.forEach(ors -> {
            if (!ors.isEmpty()) {
                sql.OR().WHERE(ors.toArray(new String[0]));
            }
        });
        BuilderUtil.remove();
        return sql.toString();
    }

    public UpdateBuilder() {
        BuilderUtil.set(this);
    }

    @Override
    public <R> String getColumn(SerializableFunction<R, ?> function) {
        return FunctionUtil.getMetaColumn(function).wrapColumn();
    }

    public UpdateBuilder allowNull(boolean allowNull) {
        this.allowNull = allowNull;
        return this;
    }

    public UpdateBuilder updateTable(Class<?> updateClass) {
        updateTable(AnnoUtil.getTableName(updateClass));
        return this;
    }

    public UpdateBuilder updateTable(String table) {
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
        if (flag && (ObjectUtil.isNotEmpty(param) || allowNull)) {
            String wrapped = SqlConstant.wrapBackQuote(column);
            String resolve = ConditionType.resolve(ConditionType.EQ, wrapped, param, paramMap);
            setList.add(resolve);
        }
        return this;
    }

    public int execute() {
        return ObjectUtil.isNotEmpty(sql) ? SqlSessionUtil.update(build(), paramMap) : 0;
    }


}