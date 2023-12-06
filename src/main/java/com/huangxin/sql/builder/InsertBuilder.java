package com.huangxin.sql.builder;

import cn.hutool.core.util.ReflectUtil;
import com.huangxin.sql.entity.BaseBuilder;
import com.huangxin.sql.type.WrapType;
import com.huangxin.sql.util.CommonUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * InsertBuilder
 *
 * @author 黄鑫
 */
public class InsertBuilder extends BaseBuilder {

    private final Insert insert;
    private final ExpressionList rows = new ExpressionList().withUsingBrackets(false);

    public InsertBuilder() {
        insert = new Insert();
    }

    public InsertBuilder(Insert insert) {
        this.insert = insert;
    }

    public InsertBuilder(String sql) {
        try {
            insert = (Insert) CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Insert build() {
        return insert;
    }

    @Override
    public String toString() {
        return build().toString();
    }

    public <T> InsertBuilder insert(T t) {
        return insertBatch(Collections.singletonList(t));
    }

    public <T> InsertBuilder insertBatch(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return this;
        }

        T firstItem = iterator.next();
        Table table = getTable(firstItem.getClass());
        if (insert.getTable() != null && !insert.getTable().equals(table)) {
            rows.withExpressions(new ArrayList<>());
        }
        insert.withTable(table).withSelect(new Select().withSelectBody(new SetOperationList().addBrackets(false).addSelects(new ValuesStatement(rows))));
        List<Field> fields = CommonUtil.getColumnFields(firstItem.getClass());
        List<Column> columnList = fields.stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(this::getColumn)
                .collect(Collectors.toList());
        insert.withColumns(columnList);
        insertValues(firstItem, fields);

        while (iterator.hasNext()) {
            T t = iterator.next();
            insertValues(t, fields);
        }
        return this;
    }

    private <T> void insertValues(T item, List<Field> fields) {
        List<Expression> expressionList = fields.stream()
                .map(field -> WrapType.getWrapExpression(this, ReflectUtil.getFieldValue(item, field)))
                .collect(Collectors.toList());
        RowConstructor rowConstructor = new RowConstructor().withExprList(new ExpressionList(expressionList));
        rows.addExpressions(rowConstructor);
    }
}
