package com.huangxin.sql.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.type.JoinType;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * JoinBuild
 *
 * @author 黄鑫
 */
public class JoinBuilder extends AbstractConditionBuilder<JoinBuilder> {

    protected final Table joinTable;
    protected final JoinType joinType;

    public JoinBuilder(AbstractConditionBuilder<?> builder, JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        this(builder, joinType, joinClass, null, consumer);
    }

    public JoinBuilder(AbstractConditionBuilder<?> builder, JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        this.paramMap = builder.getParamMap();
        this.tableMap = builder.getTableMap();
        this.joinType = joinType;
        this.joinTable = getTable(joinClass);
        this.consumer = consumer;
        if (StrUtil.isNotEmpty(joinAlias)) {
            joinTable.withAlias(new Alias(joinAlias));
        }
    }

    @Override
    public Join build() {
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(typeThis));
        Expression expression = expressionList.stream().reduce((left, right) -> {
            if (orList.contains(right)) {
                return new OrExpression(left, right);
            }
            return new AndExpression(left, right);
        }).orElse(null);
        return joinType.exec(new Join()).withRightItem(joinTable).addOnExpression(expression);
    }

}
