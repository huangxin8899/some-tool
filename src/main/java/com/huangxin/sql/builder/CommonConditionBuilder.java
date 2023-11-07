package com.huangxin.sql.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.constant.SqlConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * SelectAndUpdateConditionBuilder
 *
 * @author 黄鑫
 */
public abstract class CommonConditionBuilder<T extends ConditionBuilder<T>> extends AbstractConditionBuilder<T> {

    protected Consumer<T> consumer;

    @Override
    public String build() {
        StringBuilder joinSegment = new StringBuilder();
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(thisType));
        String conditionStr = String.join(SqlConstant._AND_, whereList);
        joinSegment.append(conditionStr);
        orNestList.forEach(s -> {
            String orStr = String.join(SqlConstant._AND_, s);
            joinSegment.append(StrUtil.format(" OR ({})", orStr));
        });
        return joinSegment.toString();
    }

    public T or(Consumer<ConditionBuilder<T>> consumer) {
        return or(true, consumer);
    }

    public T or(boolean flag, Consumer<ConditionBuilder<T>> consumer) {
        if (flag) {
            try {
                isOr = Boolean.TRUE;
                orNestList.add(new ArrayList<>());
                consumer.accept(thisType);
            } finally {
                isOr = Boolean.FALSE;
            }
        }
        return thisType;
    }

    public T and(Consumer<ConditionBuilder<T>> consumer) {
        return and(true, consumer);
    }

    public T and(boolean flag, Consumer<ConditionBuilder<T>> consumer) {
        if (flag) {
            try {
                isAnd = Boolean.TRUE;
                consumer.accept(thisType);
            } finally {
                String andStr = String.join(" AND ", andMap.get("and"));
                String orStr = String.join(" OR ", andMap.get("or"));
                String merge = StrUtil.format("({} OR {})", andStr, orStr);
                if (isOr) {
                    List<String> list = orNestList.get(orNestList.size() - 1);
                    Optional.ofNullable(merge).ifPresent(list::add);
                } else {
                    Optional.ofNullable(merge).ifPresent(whereList::add);
                }
                isAnd = Boolean.FALSE;
            }
        }
        return thisType;
    }
}
