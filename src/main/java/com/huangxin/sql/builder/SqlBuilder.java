package com.huangxin.sql.builder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.huangxin.sql.entity.MetaColumn;
import com.huangxin.sql.anno.Condition;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.util.SqlSessionUtil;
import com.huangxin.sql.type.ConditionType;
import com.huangxin.sql.util.AnnoUtil;
import com.huangxin.sql.util.ScriptUtil;

import java.io.Serializable;
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
            fields.stream().filter(field -> field.isAnnotationPresent(Condition.class)).forEach(field -> {
                Condition condition = field.getAnnotation(Condition.class);
                String columnName = ObjectUtil.isNotEmpty(condition.fieldName()) ? condition.fieldName() : MetaColumn.ofField(field).getColumnName();
                ConditionType type = condition.type();
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
        return update(BuilderConfig.ALLOW_NULL, tableClass);
    }

    public static <T> UpdateBuilder update(boolean allowNull, Class<T> tableClass) {
        return new UpdateBuilder().updateTable(tableClass).allowNull(allowNull);
    }

    public static <T> UpdateBuilder update(String table) {
        return update(BuilderConfig.ALLOW_NULL, table);
    }

    public static <T> UpdateBuilder update(boolean allowNull, String table) {
        return new UpdateBuilder().updateTable(table).allowNull(allowNull);
    }

    public static <T> UpdateBuilder updateById(T t) {
        return updateById(BuilderConfig.ALLOW_NULL, t);
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
        return updateBatchByIdAndExecute(BuilderConfig.ALLOW_NULL, collection);
    }

    public static <T> int updateBatchByIdAndExecute(Collection<T> collection, int batchSize) {
        return updateBatchByIdAndExecute(BuilderConfig.ALLOW_NULL, collection, batchSize);
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
        return split.stream().mapToInt(list -> SqlSessionUtil.update(scrip, list)).sum();
    }


    public static <T> DeleteBuilder delete(Class<T> tableClass) {
        return new DeleteBuilder().deleteTable(tableClass);
    }

    public static <T> DeleteBuilder delete(boolean login, Class<T> tableClass) {
        return new DeleteBuilder().deleteTable(tableClass).login(login);
    }

    public static DeleteBuilder delete(String table) {
        return new DeleteBuilder().deleteTable(table);
    }

    public static DeleteBuilder delete(boolean login, String table) {
        return new DeleteBuilder().deleteTable(table).login(login);
    }

    public static <T> DeleteBuilder deleteById(Class<T> tableClass, Serializable id) {
        return new DeleteBuilder()
                .deleteTable(tableClass)
                .eq(ObjectUtil.isNotEmpty(id), AnnoUtil.getPrimaryColumn(tableClass), id);
    }

    public static <T> DeleteBuilder deleteById(boolean login, Class<T> tableClass, Serializable id) {
        return new DeleteBuilder()
                .deleteTable(tableClass)
                .login(login)
                .eq(ObjectUtil.isNotEmpty(id), AnnoUtil.getPrimaryColumn(tableClass), id);
    }

    public static <T> DeleteBuilder deleteByIds(Class<T> tableClass, Collection<? extends Serializable> idList) {
        return new DeleteBuilder()
                .deleteTable(tableClass)
                .in(ObjectUtil.isNotEmpty(idList), AnnoUtil.getPrimaryColumn(tableClass), idList);
    }

    public static <T> DeleteBuilder deleteByIds(boolean login, Class<T> tableClass, Collection<? extends Serializable> idList) {
        return new DeleteBuilder()
                .deleteTable(tableClass)
                .login(login)
                .in(ObjectUtil.isNotEmpty(idList), AnnoUtil.getPrimaryColumn(tableClass), idList);
    }

}
