package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ObjectUtil;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.executor.SqlExecutor;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.SerializableFunction;

/**
 * DeleteBuilder
 *
 * @author 黄鑫
 */
public class DeleteBuilder extends CommonConditionBuilder<DeleteBuilder> {

    @Override
    public String build() {
        sql.DELETE_FROM(table);
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

    public DeleteBuilder delete(Class<?> deleteClass) {
        return delete(AnnoUtil.getTableName(deleteClass));
    }

    public DeleteBuilder delete(String table) {
        this.table = SqlConstant.wrapBackQuote(table);
        return this;
    }

    public int execute() {
        return ObjectUtil.isNotEmpty(sql) ? SqlExecutor.delete(build(), paramMap) : 0;
    }
}
