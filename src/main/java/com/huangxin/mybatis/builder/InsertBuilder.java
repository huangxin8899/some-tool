package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.SqlConstant;
import com.huangxin.mybatis.util.AnnoUtil;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * InsertBuilder
 *
 * @author 黄鑫
 */
public class InsertBuilder extends SqlEntity implements Builder {

    @Override
    public String build() {
        return sql.toString();
    }

    public <R> InsertBuilder insert(R r) {
        return insertBatch(Collections.singletonList(r));
    }

    public <R> InsertBuilder insertBatch(Collection<R> collection) {
        Iterator<R> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return this;
        }

        R firstItem = iterator.next();
        String tableName = AnnoUtil.getTableName(firstItem.getClass());
        Field[] fields = ReflectUtil.getFields(firstItem.getClass(), field -> !Modifier.isStatic(field.getModifiers()));
        String intoColumn = Arrays.stream(fields).map(field -> MetaColumn.ofField(field).wrapColumn()).collect(Collectors.joining(", "));
        sql.INSERT_INTO(tableName).INTO_COLUMNS(intoColumn);
        insertValues(firstItem, fields);

        while (iterator.hasNext()) {
            R r = iterator.next();
            insertValues(r, fields);
        }
        return this;
    }

    private <R> void insertValues(R firstItem, Field[] fields) {
        Arrays.stream(fields)
                .map(ReflectUtil::getFieldName)
                .map(fieldName -> ReflectUtil.getFieldValue(firstItem, fieldName))
                .forEach(fieldValue -> {
                    String paramName = SqlConstant.ARG + paramMap.size();
                    sql.INTO_VALUES(StrUtil.format("'{}'", SqlConstant.wrapParam(paramName)));
                    paramMap.put(paramName, fieldValue);
                });
        sql.ADD_ROW();
    }
}
