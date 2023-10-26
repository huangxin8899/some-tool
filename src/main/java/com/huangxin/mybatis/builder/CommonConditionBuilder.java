package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.ConditionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * SelectAndUpdateConditionBuilder
 *
 * @author 黄鑫
 */
public abstract class CommonConditionBuilder<T extends AbstractConditionBuilder<T>> extends AbstractConditionBuilder<T> {

    private final T thisType = (T) this;

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
            String resolve = ConditionType.resolve(conditionType, column, param, paramMap);
            if (isOr) {
                List<String> list = orList.get(orList.size() - 1);
                Optional.ofNullable(resolve).ifPresent(list::add);
            } else {
                Optional.ofNullable(resolve).ifPresent(whereList::add);
            }
        }
        return thisType;
    }

    @Override
    public T or(boolean flag, Consumer<T> consumer) {
        if (flag) {
            try {
                isOr = Boolean.TRUE;
                orList.add(new ArrayList<>());
                consumer.accept(thisType);
            } finally {
                isOr = Boolean.FALSE;
            }
        }
        return thisType;
    }
}
