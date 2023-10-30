package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.executor.SqlExecutor;
import com.huangxin.mybatis.util.AnnoUtil;

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

    public <T> InsertBuilder insert(T t) {
        return insertBatch(Collections.singletonList(t));
    }

    public <T> InsertBuilder insertBatch(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return this;
        }

        T firstItem = iterator.next();
        String tableName = AnnoUtil.getTableName(firstItem.getClass());
        Field[] fields = ReflectUtil.getFields(firstItem.getClass(), field -> !Modifier.isStatic(field.getModifiers()));
        String intoColumn = Arrays.stream(fields).map(field -> MetaColumn.ofField(field).wrapColumn()).collect(Collectors.joining(", "));
        sql.INSERT_INTO(tableName).INTO_COLUMNS(intoColumn);
        insertValues(firstItem, fields);

        while (iterator.hasNext()) {
            T t = iterator.next();
            insertValues(t, fields);
        }
        return this;
    }

    private <T> void insertValues(T firstItem, Field[] fields) {
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

    public int execute() {
        return ObjectUtil.isNotEmpty(sql) ? SqlExecutor.insert(build(), paramMap) : 0;
    }
}
