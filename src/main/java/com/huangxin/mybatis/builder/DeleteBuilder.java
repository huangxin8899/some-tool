package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.config.BuilderConfig;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.executor.SqlExecutor;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.func.SerializableFunction;

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
        return sql.toString();
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
        return ObjectUtil.isNotEmpty(sql) ? SqlExecutor.delete(build(), paramMap) : 0;
    }
}
