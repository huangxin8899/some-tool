package com.huangxin.sql.expression;

import cn.hutool.core.util.StrUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

/**
 * MybatisExpression
 *
 * @author 黄鑫
 */
public class MybatisExpression extends ASTNodeAccessImpl implements Expression {

    private String name;

    public MybatisExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
    }

    @Override
    public String toString() {
        return StrUtil.format("#{{}}", name);
    }


}
