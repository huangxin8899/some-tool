package com.huangxin.sql.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.anno.SelectColumn;
import com.huangxin.sql.builder.join.*;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.type.JoinType;
import com.huangxin.sql.util.CommonUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SelectBuilder
 *
 * @author 黄鑫
 */
public class SelectBuilder
        extends AbstractConditionBuilder<SelectBuilder>
        implements JoinModel<SelectBuilder> {

    private final PlainSelect plainSelect;

    public SelectBuilder() {
        this.plainSelect = new PlainSelect();
    }

    public SelectBuilder(PlainSelect plainSelect) {
        this.plainSelect = plainSelect;
    }

    public SelectBuilder(String sql) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            this.plainSelect = (PlainSelect) select.getSelectBody();
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlainSelect build() {
        if (!orList.isEmpty() && orList.contains(expressionList.get(0))) {
            throw new IllegalArgumentException("嵌套查询第一个条件不能是or");
        }
        return plainSelect.withWhere(expressionList.stream().reduce((left, right) -> {
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

    @Override
    public SelectBuilder join(boolean flag, JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        if (flag) {
            Join join = new JoinBuilder(this, joinType, joinClass, joinAlias, consumer).build();
            plainSelect.addJoins(join);
        }
        return this;
    }

    @SafeVarargs
    public final <R> SelectBuilder select(SerializableFunction<R, ?>... functions) {
        List<SelectExpressionItem> selectItems = Arrays.stream(functions)
                .map(function -> new SelectExpressionItem(getColumn(function)))
                .collect(Collectors.toList());
        plainSelect.addSelectItems(selectItems);
        return this;
    }

    public SelectBuilder select(String... selects) {
        List<SelectExpressionItem> selectItems = Arrays.stream(selects)
                .map(selectStr -> new SelectExpressionItem(new Column(selectStr)))
                .collect(Collectors.toList());
        plainSelect.addSelectItems(selectItems);
        return this;
    }

    public <R> SelectBuilder select(SerializableFunction<R, ?> function, String alias) {
        Column column = getColumn(function);
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(column);
        Optional.ofNullable(alias).map(Alias::new).ifPresent(selectExpressionItem::withAlias);
        plainSelect.addSelectItems(selectExpressionItem);
        return this;
    }

    public SelectBuilder select(Class<?> rClass) {
        List<Field> fields = CommonUtil.getFields(rClass, field -> !Modifier.isStatic(field.getModifiers()) &&
                !Modifier.isFinal(field.getModifiers()) &&
                field.isAnnotationPresent(SelectColumn.class));
        List<SelectExpressionItem> selectItems = fields.stream()
                .map(field -> {
                    SelectColumn selectColumn = field.getAnnotation(SelectColumn.class);
                    if (!selectColumn.table().equals(Void.class)) {
                        Table targetTable = getTable(selectColumn.table());
                        return new SelectExpressionItem(new Column(targetTable, StrUtil.toUnderlineCase(field.getName())));
                    } else if (StrUtil.isNotEmpty(selectColumn.value())) {
                        return new SelectExpressionItem(new Column(selectColumn.value()));
                    } else {
                        throw new IllegalArgumentException("SelectColumn.value不可为空");
                    }
                }).collect(Collectors.toList());
        plainSelect.addSelectItems(selectItems);
        return this;
    }


    public SelectBuilder from(Class<?> tClass) {
        return from(tClass, null);
    }

    public SelectBuilder from(Class<?> tClass, String alias) {
        Table table = getTable(tClass);
        if (StrUtil.isNotEmpty(alias)) {
            table.withAlias(new Alias(alias));
        }
        plainSelect.withFromItem(table);
        return this;
    }

    public SelectBuilder having(Consumer<HavingBuilder> consumer) {
        return having(true, consumer);
    }

    public SelectBuilder having(boolean flag, Consumer<HavingBuilder> consumer) {
        if (flag) {
            Expression expression = plainSelect.getHaving();
            if (expression != null) {
                plainSelect.withHaving(new AndExpression(expression, new HavingBuilder(this, consumer).build()));
            } else {
                plainSelect.withHaving(new HavingBuilder(this, consumer).build());
            }
        }
        return this;
    }

    @SafeVarargs
    public final <R> SelectBuilder groupBy(SerializableFunction<R, ?>... functions) {
        return groupBy(true, functions);
    }

    @SafeVarargs
    public final <R> SelectBuilder groupBy(boolean flag, SerializableFunction<R, ?>... functions) {
        if (flag) {
            Arrays.stream(functions)
                    .map(this::getColumn)
                    .forEach(plainSelect::addGroupByColumnReference);
        }
        return this;
    }

    @SafeVarargs
    public final <R> SelectBuilder orderByAsc(SerializableFunction<R, ?>... functions) {
        return orderByAsc(true, functions);
    }

    @SafeVarargs
    public final <R> SelectBuilder orderByAsc(boolean flag, SerializableFunction<R, ?>... functions) {
        return orderBy(flag, true, functions);
    }

    @SafeVarargs
    public final <R> SelectBuilder orderByDesc(SerializableFunction<R, ?>... functions) {
        return orderByDesc(true, functions);
    }

    @SafeVarargs
    public final <R> SelectBuilder orderByDesc(boolean flag, SerializableFunction<R, ?>... functions) {
        return orderBy(flag, false, functions);
    }

    @SafeVarargs
    public final <R> SelectBuilder orderBy(boolean flag, boolean isAsc, SerializableFunction<R, ?>... functions) {
        if (flag) {
            List<OrderByElement> orderedElements = Arrays.stream(functions)
                    .map(this::getColumn)
                    .map(column -> new OrderByElement().withAsc(isAsc).withExpression(column))
                    .collect(Collectors.toList());
            plainSelect.addOrderByElements(orderedElements);
        }
        return this;
    }

    public SelectBuilder limit(long rowCount) {
        plainSelect.withLimit(new Limit().withRowCount(new LongValue(rowCount)));
        return this;
    }

    public SelectBuilder limit(long offset, long rowCount) {
        plainSelect.withLimit(new Limit().withRowCount(new LongValue(rowCount)).withOffset(new LongValue(offset)));
        return this;
    }

}
