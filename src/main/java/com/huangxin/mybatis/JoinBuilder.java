package com.huangxin.mybatis;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.JoinType;
import com.huangxin.mybatis.util.SerializableFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * AbstractJoin
 *
 * @author 黄鑫
 */
public class JoinBuilder implements ConditionBuilder<JoinBuilder> {

    private final SqlBuilder sqlBuilder;
    private final SerializableFunction<?, ?> lFunc;
    private final SerializableFunction<?, ?> rFunc;
    private final Consumer<JoinBuilder> consumer;

    private final List<String> whereList = new ArrayList<>();
    private final List<List<String>> orList = new ArrayList<>();
    private Boolean isOr = Boolean.FALSE;

    public JoinBuilder(SqlBuilder sqlBuilder, SerializableFunction<?, ?> lFunc, SerializableFunction<?, ?> rFunc) {
        this.sqlBuilder = sqlBuilder;
        this.lFunc = lFunc;
        this.rFunc = rFunc;
        this.consumer = join -> {};
    }

    public JoinBuilder(SqlBuilder sqlBuilder, SerializableFunction<?, ?> lFunc, SerializableFunction<?, ?> rFunc, Consumer<JoinBuilder> consumer) {
        this.sqlBuilder = sqlBuilder;
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

    @Override
    public JoinBuilder apply(boolean flag, String applySql, Object... params) {
        if (flag) {
            Optional.ofNullable(StrUtil.format(applySql, params)).ifPresent(whereList::add);
        }
        return this;
    }

    @Override
    public JoinBuilder apply(boolean flag, ConditionType conditionType, String column, Object param) {
        if (flag) {
            String resolve = ConditionType.resolve(conditionType, column, param, sqlBuilder.getParamMap());
            if (isOr) {
                List<String> list = orList.get(orList.size() - 1);
                Optional.ofNullable(resolve).ifPresent(list::add);
            } else {
                Optional.ofNullable(resolve).ifPresent(whereList::add);
            }
        }
        return this;
    }

    @Override
    public JoinBuilder or(boolean flag, Consumer<JoinBuilder> consumer) {
        try {
            isOr = Boolean.TRUE;
            orList.add(new ArrayList<>());
            consumer.accept(this);
        } finally {
            isOr = Boolean.FALSE;
        }
        return this;
    }

    private void WHERE(String... conditions) {
        whereList.addAll(Arrays.asList(conditions));
    }

    private void OR(String... conditions) {
        orList.add(Arrays.asList(conditions));
    }
}
