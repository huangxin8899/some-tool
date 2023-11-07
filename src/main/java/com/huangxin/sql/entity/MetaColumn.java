package com.huangxin.sql.entity;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.anno.Column;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.util.AnnoUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MetaColumn
 *
 * @author 黄鑫
 */
@Data
public class MetaColumn {

    private String tableName;
    private String columnName;
    private Class<?> tableClass;

    private static final Map<Field, MetaColumn> COLUMN_MAP = new ConcurrentHashMap<>();


    public static MetaColumn ofField(Field field) {
        return COLUMN_MAP.getOrDefault(field, getMetaColumn(field));
    }

    @NotNull
    private static MetaColumn getMetaColumn(Field field) {
        Class<?> tableClass = field.getDeclaringClass();
        String tableName = AnnoUtil.getTableName(tableClass);
        String columnName;
        if (field.isAnnotationPresent(Column.class)) {
            Column resultField = field.getAnnotation(Column.class);
            // 表名
            if (StrUtil.isNotEmpty(resultField.table())) {
                tableName = resultField.table();
            }
            // 字段名
            if (StrUtil.isNotEmpty(resultField.value())) {
                columnName = resultField.value();
            } else {
                columnName = StrUtil.toUnderlineCase(field.getName());
            }
        } else {
            columnName = StrUtil.toUnderlineCase(field.getName());
        }
        MetaColumn metaColumn = new MetaColumn();
        metaColumn.setTableName(tableName);
        metaColumn.setColumnName(columnName);
        metaColumn.setTableClass(tableClass);
        return metaColumn;
    }


    public String wrapTableDotColumn() {
        StringBuilder segment = new StringBuilder();
        Optional.ofNullable(tableName).ifPresent(s -> segment.append(SqlConstant.wrapBackQuote(s)).append(StrUtil.DOT));
        segment.append(SqlConstant.wrapBackQuote(columnName));
        return segment.toString();
    }

    public String wrapTableDotColumn(Map<Class<?>, String> aliasMap) {
        StringBuilder segment = new StringBuilder();
        String targetTableName = aliasMap.getOrDefault(tableClass, tableName);
        Optional.ofNullable(targetTableName).ifPresent(s -> segment.append(SqlConstant.wrapBackQuote(s)).append(StrUtil.DOT));
        segment.append(SqlConstant.wrapBackQuote(columnName));
        return segment.toString();
    }

    public String wrapTableDotColumnAsColumn() {
        return StrUtil.format("{} AS {}", wrapTableDotColumn(), SqlConstant.wrapBackQuote(columnName));
    }

    public String wrapTableAsTable() {
        String wrapped = SqlConstant.wrapBackQuote(tableName);
        return StrUtil.format("{} AS {}", wrapped, wrapped);
    }

    public String wrapTableAsTable(Map<Class<?>, String> aliasMap) {
        String wrapped = SqlConstant.wrapBackQuote(tableName);
        String alias = SqlConstant.wrapBackQuote(aliasMap.getOrDefault(tableClass, tableName));
        return StrUtil.format("{} AS {}", wrapped, alias);
    }

    public String wrapColumn() {
        return SqlConstant.wrapBackQuote(this.columnName);
    }
}
