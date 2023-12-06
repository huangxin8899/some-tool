package com.huangxin.sql.expression;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

/**
 * StringExpression
 *
 * @author 黄鑫
 */
public class StringExpression extends ASTNodeAccessImpl implements Expression {

    private Object value;

    public StringExpression(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {

    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
