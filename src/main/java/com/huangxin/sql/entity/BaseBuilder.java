package com.huangxin.sql.entity;

import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.util.CommonUtil;
import com.huangxin.sql.util.SFuncUtil;
import lombok.Getter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * SqlEntity
 *
 * @author 黄鑫
 */
@Getter
public abstract class BaseBuilder {

    protected Map<String, Object> paramMap = new HashMap<>();
    protected Map<Class<?>, Table> tableMap = new HashMap<>();

    public abstract Object build();

    public String nextParamName(Object param) {
        String nextKey = "param" + paramMap.size();
        paramMap.put(nextKey, param);
        return nextKey;
    }

    public Table getTable(Class<?> tableClass) {
        return putIfAbsent(tableClass, new Table(CommonUtil.getTableName(tableClass)));
    }

    public <R> Table getTable(SerializableFunction<R, ?> function) {
        MetaColumn metaColumn = SFuncUtil.getMetaColumn(function);
        return putIfAbsent(metaColumn.getTableClass(), new Table(metaColumn.getTableName()));
    }

    public Table getTable(Class<?> tableClass, Table table) {
        return putIfAbsent(tableClass, table);
    }

    public <R> Column getColumn(SerializableFunction<R, ?> function) {
        MetaColumn metaColumn = SFuncUtil.getMetaColumn(function);
        return new Column(putIfAbsent(metaColumn.getTableClass(), new Table(metaColumn.getTableName())), metaColumn.getColumnName());
    }

    public Column getColumn(Field field) {
        MetaColumn metaColumn = MetaColumn.ofField(field);
        return new Column(metaColumn.getColumnName());
    }

    private Table putIfAbsent(Class<?> tableClass, Table table) {
        return Optional.ofNullable(tableMap.get(tableClass)).orElseGet(() -> {
            tableMap.put(tableClass, table);
            return table;
        });
    }
}
