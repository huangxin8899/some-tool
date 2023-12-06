package com.huangxin.sql.builder;

import cn.hutool.core.util.ObjectUtil;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.type.WrapType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;

/**
 * UpdateBuilder
 *
 * @author 黄鑫
 */
public class UpdateBuilder extends AbstractConditionBuilder<UpdateBuilder> {

    private final Update update;
    private boolean allowNull = BuilderConfig.ALLOW_NULL;

    public UpdateBuilder() {
        this.update = new Update();
    }

    public UpdateBuilder(Update update) {
        this.update = update;
    }
    
    public UpdateBuilder(String sql) {
        try {
            update = (Update) CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdateBuilder allowNull(boolean allowNull) {
        this.allowNull = allowNull;
        return this;
    }

    @Override
    public Object build() {
        return update.withWhere(expressionList.stream().reduce((left, right) -> {
            if (orList.contains(right)) {
                return new OrExpression(left, right);
            }
            return new AndExpression(left, right);
        }).orElse(null));
    }

    @Override
    public String toString() {
        return build().toString();
    }

    public UpdateBuilder updateTable(Class<?> updateClass) {
        update.withTable(getTable(updateClass));
        return this;
    }

    public <R> UpdateBuilder set(SerializableFunction<R, ?> function, Object param) {
        return set(true, function, param);
    }

    public <R> UpdateBuilder set(boolean flag, SerializableFunction<R, ?> function, Object param) {
        if (flag && (ObjectUtil.isNotEmpty(param) || allowNull)) {
            update.addUpdateSet(getColumn(function), WrapType.getWrapExpression(this, param));
        }
        return this;
    }
}
