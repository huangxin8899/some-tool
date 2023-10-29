package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.SqlConstant;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * OrBuilder
 *
 * @author huangxin
 */
public class OrBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<OrBuilder<T>> {

    protected Consumer<OrBuilder<T>> consumer;

    public OrBuilder(Consumer<OrBuilder<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public String build() {
        StringBuilder joinSegment = new StringBuilder();
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(this));
        String conditionStr = String.join(SqlConstant._AND_, whereList);
        joinSegment.append(conditionStr);
        orNestList.forEach(s -> {
            String orStr = String.join(SqlConstant._AND_, s);
            joinSegment.append(StrUtil.format(" OR ({})", orStr));
        });
        return joinSegment.toString();
    }
}
