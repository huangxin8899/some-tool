package com.huangxin.sql.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.entity.SqlEntity;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.type.ConditionType;
import com.huangxin.sql.util.FunctionUtil;
import lombok.Getter;

import java.util.*;

/**
 * AbstractConditionBuilder
 *
 * @author 黄鑫
 */
public abstract class AbstractConditionBuilder<T extends ConditionBuilder<T>>
        extends SqlEntity
        implements ConditionBuilder<T> {

    protected final List<String> whereList = new ArrayList<>();
    protected final Map<String, List<String>> andMap = createAndMap();
    protected final List<List<String>> orNestList = new ArrayList<>();
    protected Boolean isOr = Boolean.FALSE;
    protected Boolean isAnd = Boolean.FALSE;
    @Getter
    protected Map<Class<?>, String> aliasMap = new HashMap<>();

    protected final T thisType = (T) this;


    @Override
    public <R> String getColumn(SerializableFunction<R, ?> function) {
        return FunctionUtil.getMetaColumn(function).wrapTableDotColumn(aliasMap);
    }

    @Override
    public T apply(boolean flag, String applySql, Object... params) {
        if (flag) {
            Optional.ofNullable(StrUtil.format(applySql, params)).ifPresent(whereList::add);
        }
        return thisType;
    }

    @Override
    public T apply(boolean flag, ConditionType conditionType, String column, Object param) {
        if (flag) {
            String resolve = ConditionType.resolve(conditionType, column, param, this);
            if (isAnd) {
                if (isOr) {
                    Optional.ofNullable(resolve).ifPresent(str -> andMap.get("or").add(str));
                } else {
                    Optional.ofNullable(resolve).ifPresent(str -> andMap.get("and").add(str));
                }
                return thisType;
            }
            if (isOr) {
                List<String> list = orNestList.get(orNestList.size() - 1);
                Optional.ofNullable(resolve).ifPresent(list::add);
            } else {
                Optional.ofNullable(resolve).ifPresent(whereList::add);
            }
        }
        return thisType;
    }

    public T resultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
        return thisType;
    }

    private Map<String, List<String>> createAndMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("or", new ArrayList<>());
        map.put("and", new ArrayList<>());
        return map;
    }
}
