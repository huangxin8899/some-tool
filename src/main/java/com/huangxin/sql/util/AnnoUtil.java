package com.huangxin.sql.util;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.entity.MetaColumn;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.anno.Id;
import com.huangxin.sql.anno.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class AnnoUtil {


    public static String getTableName(Class<?> aClass) {
        if (aClass.isAnnotationPresent(Table.class)) {
            return aClass.getAnnotation(Table.class).value();
        }
        return StrUtil.toUnderlineCase(aClass.getSimpleName());
    }

    public static String wrapTableAsTable(Class<?> aClass) {
        String tableName = SqlConstant.wrapBackQuote(getTableName(aClass));
        return StrUtil.format("{} AS {}", tableName, tableName);
    }

    public static String wrapTableAsAlias(Class<?> aClass, String alias) {
        String tableName = SqlConstant.wrapBackQuote(getTableName(aClass));
        return StrUtil.format("{} AS {}", tableName, alias);
    }

    /**
     * 获取主键变量名
     *
     * @param aClass
     * @return
     */
    public static String getPrimaryName(Class<?> aClass) {
        List<Field> fields = getFields(aClass, new ArrayList<>());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getName();
            }
        }
        return "id";
    }

    /**
     * 获取主键字段名
     *
     * @param aClass
     * @return
     */
    public static String getPrimaryColumn(Class<?> aClass) {
        List<Field> fields = getFields(aClass, new ArrayList<>());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                MetaColumn metaColumn = MetaColumn.ofField(field);
                return metaColumn.getColumnName();
            }
        }
        return "id";
    }

    public static List<Field> getFields(Class<?> clazz, List<Field> fields) {
        if (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                fields.add(field);
            }
            getFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}