package com.huangxin.sql.builder;

import com.huangxin.sql.entity.BaseBuilder;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.type.ConditionType;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.schema.Column;

import java.util.*;
import java.util.function.Consumer;

/**
 * AbstractConditionBuilder
 *
 * @author 黄鑫
 */
public abstract class AbstractConditionBuilder<T extends AbstractConditionBuilder<T>>
        extends BaseBuilder
        implements ConditionBuilder<T> {

    protected T typeThis = (T) this;
    protected Consumer<T> consumer;
    protected List<Expression> expressionList = new ArrayList<>();
    protected Set<Expression> orList = new HashSet<>();

    @Override
    public T and(boolean flag, Consumer<AndBuilder> andConsumer) {
        expressionList.add(new Parenthesis(new AndBuilder(typeThis, andConsumer).build()));
        return typeThis;
    }

    @Override
    public T or(boolean flag, Consumer<OrBuilder> orConsumer) {
        Parenthesis parenthesis = new Parenthesis(new OrBuilder(typeThis, orConsumer).build());
        expressionList.add(parenthesis);
        orList.add(parenthesis);
        return typeThis;
    }

    @Override
    public <R> Column toColumn(SerializableFunction<R, ?> function) {
        return getColumn(function);
    }

    @Override
    public T apply(boolean flag, ConditionType conditionType, Column column, Object param) {
        if (flag) {
            expressionList.add(ConditionType.resolve(conditionType, column, param, this));
        }
        return typeThis;
    }

    protected Expression buildExpression() {
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(typeThis));
        if (!orList.isEmpty() && orList.contains(expressionList.get(0))) {
            throw new IllegalArgumentException("嵌套查询第一个条件不能是or");
        }
        return expressionList.stream().reduce((left, right) -> {
            if (orList.contains(right)) {
                return new OrExpression(left, right);
            }
            return new AndExpression(left, right);
        }).orElse(null);
    }
}
