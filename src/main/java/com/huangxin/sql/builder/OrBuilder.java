package com.huangxin.sql.builder;

import net.sf.jsqlparser.expression.Expression;

import java.util.function.Consumer;

/**
 * OrBuilder
 *
 * @author huangxin
 */
public class OrBuilder extends AbstractConditionBuilder<OrBuilder> {


    public OrBuilder(AbstractConditionBuilder<?> builder, Consumer<OrBuilder> consumer) {
        this.paramMap = builder.getParamMap();
        this.tableMap = builder.getTableMap();
        this.consumer = consumer;
    }


    @Override
    public Expression build() {
        return buildExpression();
    }

}
