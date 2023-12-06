package com.huangxin.sql.entity;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.util.CommonUtil;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Map;
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

    private static MetaColumn getMetaColumn(Field field) {
        MetaColumn metaColumn = new MetaColumn();
        Class<?> tableClass = field.getDeclaringClass();
        String tableName = CommonUtil.getTableName(tableClass);
        String columnName = CommonUtil.getColumnName(field);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        metaColumn.setTableClass(tableClass);
        metaColumn.setTableName(tableName);
        metaColumn.setColumnName(columnName);
        COLUMN_MAP.put(field, metaColumn);
        return metaColumn;
    }
}
