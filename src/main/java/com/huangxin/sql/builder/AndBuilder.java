package com.huangxin.sql.builder;

import net.sf.jsqlparser.expression.Expression;

import java.util.function.Consumer;

/**
 * AndBuilder
 *
 * @author huangxin
 */
public class AndBuilder extends AbstractConditionBuilder<AndBuilder> {

    public AndBuilder(AbstractConditionBuilder<?> builder, Consumer<AndBuilder> consumer) {
        this.paramMap = builder.getParamMap();
        this.tableMap = builder.getTableMap();
        this.consumer = consumer;
    }

    @Override
    public Expression build() {
        return buildExpression();
    }
}
