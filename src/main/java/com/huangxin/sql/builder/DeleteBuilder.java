package com.huangxin.sql.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.util.BuilderUtil;
import com.huangxin.sql.util.SqlSessionUtil;
import com.huangxin.sql.util.AnnoUtil;
import com.huangxin.sql.util.FunctionUtil;
import com.huangxin.sql.func.SerializableFunction;

/**
 * DeleteBuilder
 *
 * @author 黄鑫
 */
public class DeleteBuilder extends CommonConditionBuilder<DeleteBuilder> {

    private boolean login = StrUtil.isNotEmpty(BuilderConfig.DELETE_FIELD);

    @Override
    public String build() {
        if (login) {
            sql.UPDATE(table).SET(StrUtil.format("{} = '{}'", BuilderConfig.DELETE_FIELD, BuilderConfig.DELETE_VALUE));
        } else {
            sql.DELETE_FROM(table);
        }
        whereList.forEach(sql::WHERE);
        orNestList.forEach(ors -> {
            if (!ors.isEmpty()) {
                sql.OR().WHERE(ors.toArray(new String[0]));
            }
        });
        BuilderUtil.remove();
        return sql.toString();
    }

    public DeleteBuilder() {
        BuilderUtil.set(this);
    }

    @Override
    public <R> String getColumn(SerializableFunction<R, ?> function) {
        return FunctionUtil.getMetaColumn(function).wrapColumn();
    }

    public DeleteBuilder deleteTable(Class<?> deleteClass) {
        return deleteTable(AnnoUtil.getTableName(deleteClass));
    }

    public DeleteBuilder deleteTable(String table) {
        this.table = SqlConstant.wrapBackQuote(table);
        return this;
    }

    public DeleteBuilder login(boolean login) {
        this.login = login;
        return this;
    }

    public int execute() {
        return ObjectUtil.isNotEmpty(sql) ? SqlSessionUtil.delete(build(), paramMap) : 0;
    }
}
