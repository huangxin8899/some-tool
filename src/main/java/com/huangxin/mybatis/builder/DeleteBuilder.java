package com.huangxin.mybatis.builder;

import com.huangxin.mybatis.SqlConstant;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.SerializableFunction;
import org.apache.ibatis.jdbc.SQL;

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

    public DeleteBuilder delete(Class<?> deleteClass) {
        return delete(AnnoUtil.getTableName(deleteClass));
    }

    public DeleteBuilder delete(String table) {
        this.table = SqlConstant.wrapBackQuote(table);
        return this;
    }
}
