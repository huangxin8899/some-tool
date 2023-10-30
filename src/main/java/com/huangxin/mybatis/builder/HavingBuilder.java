package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * HavingBuilder
 *
 * @author huangxin
 */
public class HavingBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<HavingBuilder<T>> {

    protected Consumer<HavingBuilder<T>> consumer;

    public HavingBuilder(Consumer<HavingBuilder<T>> consumer) {
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
