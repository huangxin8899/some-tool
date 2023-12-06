package com.huangxin.sql.builder;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * DeleteBuilder
 *
 * @author 黄鑫
 */
public class DeleteBuilder extends AbstractConditionBuilder<DeleteBuilder> {

    private final Delete delete;

    public DeleteBuilder() {
        delete = new Delete();
    }

    public DeleteBuilder(Delete delete) {
        this.delete = delete;
    }

    public DeleteBuilder(String sql) {
        try {
            delete = (Delete) CCJSqlParserUtil.parse(sql);
            if (delete.getWhere()!=null) {
                expressionList.add(delete.getWhere());
            }
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object build() {
//        Expression where = delete.getWhere();
        return delete.withWhere(expressionList.stream().reduce((left, right) -> {
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

    public DeleteBuilder deleteTable(Class<?> deleteClass) {
        delete.withTable(getTable(deleteClass));
        return this;
    }

}
