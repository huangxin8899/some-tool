package com.huangxin.sql.builder;

import net.sf.jsqlparser.expression.Expression;

import java.util.function.Consumer;

/**
 * HavingBuilder
 *
 * @author huangxin
 */
public class HavingBuilder extends AbstractConditionBuilder<HavingBuilder> {

    public HavingBuilder(AbstractConditionBuilder<?> builder, Consumer<HavingBuilder> consumer) {
        this.paramMap = builder.getParamMap();
        this.tableMap = builder.getTableMap();
        this.consumer = consumer;
    }

    @Override
    public Expression build() {
        return buildExpression();
    }
}
