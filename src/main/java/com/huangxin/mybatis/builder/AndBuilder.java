package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * AndBuilder
 *
 * @author huangxin
 */
public class AndBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<AndBuilder<T>>  {

    protected Consumer<AndBuilder<T>> consumer;

    public AndBuilder(Map<String, Object> paramMap, Consumer<AndBuilder<T>> consumer) {
        this.paramMap = paramMap;
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
