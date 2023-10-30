package com.huangxin.mybatis.builder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.huangxin.mybatis.type.ConditionType;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.anno.ConditionFlag;
import com.huangxin.mybatis.executor.SqlExecutor;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.ScriptUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * SqlBuilder
 *
 * @author 黄鑫
 */
public class SqlBuilder {

    public static SelectBuilder query() {
        return query(null);
    }

    public static <Query> SelectBuilder query(Query queryParam) {
        SelectBuilder builder = new SelectBuilder();
        if (ObjectUtil.isNotEmpty(queryParam)) {
            Class<?> paramClass = queryParam.getClass();
            builder.select(paramClass).from(paramClass);
            List<Field> fields = AnnoUtil.getFields(paramClass, new ArrayList<>());
            fields.stream().filter(field -> field.isAnnotationPresent(ConditionFlag.class)).forEach(field -> {
                ConditionFlag conditionFlag = field.getAnnotation(ConditionFlag.class);
                String columnName = ObjectUtil.isNotEmpty(conditionFlag.fieldName()) ? conditionFlag.fieldName() : MetaColumn.ofField(field).getColumnName();
                ConditionType type = conditionFlag.type();
                Object fieldValue = ReflectUtil.getFieldValue(queryParam, field);
                builder.apply(ObjectUtil.isNotEmpty(fieldValue), type, columnName, fieldValue);
            });
        }
        return builder;
    }

    public static InsertBuilder save() {
        return save(null);
    }

    public static <T> InsertBuilder save(T t) {
        InsertBuilder builder = new InsertBuilder();
        if (ObjectUtil.isNotEmpty(t)) {
            builder.insert(t);
        }
        return builder;
    }

    public static <T> InsertBuilder saveBatch(Collection<T> collection) {
        InsertBuilder builder = new InsertBuilder();
        if (CollUtil.isNotEmpty(collection)) {
            builder.insertBatch(collection);
        }
        return builder;
    }

    public static <T> UpdateBuilder update(Class<T> tableClass) {
        return update(false, tableClass);
    }

    public static <T> UpdateBuilder update(boolean allowNull, Class<T> tableClass) {
        return new UpdateBuilder().updateTable(tableClass).allowNull(allowNull);
    }

    public static <T> UpdateBuilder update(String table) {
        return update(false, table);
    }

    public static <T> UpdateBuilder update(boolean allowNull, String table) {
        return new UpdateBuilder().updateTable(table).allowNull(allowNull);
    }

    public static <T> UpdateBuilder updateById(T t) {
        return updateById(false, t);
    }

    public static <T> UpdateBuilder updateById(boolean allowNull, T t) {
        UpdateBuilder builder = new UpdateBuilder();
        if (ObjectUtil.isNotEmpty(t)) {
            Class<?> tClass = t.getClass();
            builder.updateTable(tClass).allowNull(allowNull);
            String primaryName = AnnoUtil.getPrimaryName(tClass);
            String primaryColumn = AnnoUtil.getPrimaryColumn(tClass);
            List<Field> fields = AnnoUtil.getFields(tClass, new ArrayList<>());
            fields.forEach(field -> {
                String columnName = MetaColumn.ofField(field).getColumnName();
                Object fieldValue = ReflectUtil.getFieldValue(t, field);
                builder.set(ObjectUtil.isNotEmpty(fieldValue), columnName, fieldValue);
                if (primaryName.equals(columnName)) {
                    builder.apply(ObjectUtil.isNotEmpty(fieldValue), ConditionType.EQ, primaryColumn, fieldValue);
                }
            });
        }
        return builder;
    }

    public static <T> int updateBatchByIdAndExecute(Collection<T> collection) {
        return updateBatchByIdAndExecute(false, collection);
    }

    public static <T> int updateBatchByIdAndExecute(Collection<T> collection, int batchSize) {
        return updateBatchByIdAndExecute(false, collection, batchSize);
    }

    public static <T> int updateBatchByIdAndExecute(boolean allowNull, Collection<T> collection) {
        return updateBatchByIdAndExecute(allowNull, collection, 1000);
    }

    public static <T> int updateBatchByIdAndExecute(boolean allowNull, Collection<T> collection, int batchSize) {
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return 0;
        }
        T t = iterator.next();
        String scrip = ScriptUtil.updateBatchByIdScrip(allowNull, t.getClass());
        List<List<T>> split = CollUtil.split(collection, batchSize);
        return split.stream().mapToInt(list -> SqlExecutor.update(scrip, list)).sum();
    }


}
