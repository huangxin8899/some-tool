package com.huangxin.mybatis;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.anno.Column;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.util.AnnoUtil;
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

    private static final Map<Field, MetaColumn> COLUMN_MAP = new ConcurrentHashMap<>();


    public static MetaColumn ofField(Field field) {
        return COLUMN_MAP.getOrDefault(field, getMetaColumn(field));
    }

    @NotNull
    private static MetaColumn getMetaColumn(Field field) {
        String tableName = AnnoUtil.getTableName(field.getDeclaringClass());
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
        return metaColumn;
    }


    public String wrapTableDotColumn() {
        StringBuilder segment = new StringBuilder();
        Optional.ofNullable(tableName).ifPresent(s -> segment.append(SqlConstant.wrapBackQuote(s)).append(StrUtil.DOT));
        segment.append(SqlConstant.wrapBackQuote(columnName));
        return segment.toString();
    }

    public String wrapTableDotColumnAsColumn() {
        return StrUtil.format("{} AS {}", wrapTableDotColumn(), SqlConstant.wrapBackQuote(this.columnName));
    }

    public String wrapTableAsTable() {
        String wrapped = SqlConstant.wrapBackQuote(this.tableName);
        return StrUtil.format("{} AS {}", wrapped, wrapped);
    }

    public String wrapColumn() {
        return SqlConstant.wrapBackQuote(this.columnName);
    }
}
