package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.SqlConstant;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.SerializableFunction;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * JoinBuild
 *
 * @author 黄鑫
 */
public class JoinBuilder<T extends SelectBuilder> extends CommonConditionBuilder<JoinBuilder<T>> {

    protected final T selectBuilder;
    protected final SerializableFunction<?, ?> lFunc;
    protected final SerializableFunction<?, ?> rFunc;
    protected final Consumer<JoinBuilder<T>> consumer;

    public JoinBuilder(T selectBuilder, SerializableFunction<?, ?> lFunc, SerializableFunction<?, ?> rFunc) {
        this.selectBuilder = selectBuilder;
        this.lFunc = lFunc;
        this.rFunc = rFunc;
        this.consumer = join -> {};
    }

    public JoinBuilder(T selectBuilder, SerializableFunction<?, ?> lFunc, SerializableFunction<?, ?> rFunc, Consumer<JoinBuilder<T>> consumer) {
        this.selectBuilder = selectBuilder;
        this.lFunc = lFunc;
        this.rFunc = rFunc;
        this.consumer = consumer;
    }

    @Override
    public String build() {
        StringBuilder joinSegment = new StringBuilder();
        MetaColumn lColumn = FunctionUtil.getMetaColumn(lFunc);
        String left = lColumn.wrapTableDotColumn();
        String right = FunctionUtil.getMetaColumn(rFunc).wrapTableDotColumn();
        joinSegment.append(lColumn.wrapTableAsTable()).append(SqlConstant._ON_);
        whereList.add(0, left + SqlConstant._EQUAL_ + right);
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(this));
        String conditionStr = whereList.stream().collect(Collectors.joining(SqlConstant._AND_, SqlConstant.PRE_BRACKET, SqlConstant.POST_BRACKET));
        joinSegment.append(conditionStr);
        orList.forEach(s -> {
            String orStr = String.join(SqlConstant._AND_, s);
            joinSegment.append(StrUtil.format(" OR ({})", orStr));
        });
        return joinSegment.toString();
    }

}