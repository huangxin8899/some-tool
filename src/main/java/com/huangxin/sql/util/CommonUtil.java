package com.huangxin.sql.util;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.anno.Column;
import com.huangxin.sql.anno.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CommonUtil {


    public static String getTableName(Class<?> tableClass) {
        if (!tableClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(StrUtil.format("{}未被标注为Table", tableClass.getSimpleName()));
        }
        return tableClass.getAnnotation(Table.class).value();
    }

    public static String getColumnName(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new RuntimeException(StrUtil.format("{}未被标注为Column", field.getName()));
        }
        return field.getAnnotation(Column.class).value();
    }


    public static List<Field> getColumnFields(Class<?> clazz) {
        return getFields(clazz, field ->
                !Modifier.isStatic(field.getModifiers()) &&
                !Modifier.isFinal(field.getModifiers()) &&
                field.isAnnotationPresent(Column.class));
    }

    public static List<Field> getFields(Class<?> clazz, Predicate<Field> filter) {
        List<Field> fields = new ArrayList<>();
        if (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            Arrays.stream(declaredFields)
                    .filter(filter)
                    .forEach(fields::add);
            fields.addAll(getFields(clazz.getSuperclass(), filter));
        }
        return fields;
    }
}