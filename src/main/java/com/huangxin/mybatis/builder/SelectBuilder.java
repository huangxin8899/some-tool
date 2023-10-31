package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.anno.SelectIgnore;
import com.huangxin.mybatis.executor.SqlExecutor;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.type.JoinType;
import com.huangxin.mybatis.func.SerializableFunction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * SelectBuilder
 *
 * @author 黄鑫
 */
public class SelectBuilder extends AbstractConditionBuilder<SelectBuilder> {

    protected final List<String> selectList = new ArrayList<>();
    protected final List<String> fromList = new ArrayList<>();
    protected final List<String> orList = new ArrayList<>();
    protected final List<String> andList = new ArrayList<>();
    protected final List<String> innerJoinList = new ArrayList<>();
    protected final List<String> outerJoinList = new ArrayList<>();
    protected final List<String> leftOuterJoinList = new ArrayList<>();
    protected final List<String> rightOuterJoinList = new ArrayList<>();
    protected final List<String> orderByList = new ArrayList<>();
    protected final List<String> groupByList = new ArrayList<>();
    protected final List<String> havingList = new ArrayList<>();

    @Override
    public String build() {
        selectList.forEach(sql::SELECT);
        fromList.forEach(sql::FROM);
        whereList.forEach(sql::WHERE);
        innerJoinList.forEach(sql::INNER_JOIN);
        outerJoinList.forEach(sql::OUTER_JOIN);
        leftOuterJoinList.forEach(sql::LEFT_OUTER_JOIN);
        rightOuterJoinList.forEach(sql::RIGHT_OUTER_JOIN);
        andList.forEach(andSql -> sql.AND().WHERE(andSql));
        orList.forEach(orSql -> sql.OR().WHERE(orSql));
        groupByList.forEach(sql::GROUP_BY);
        havingList.forEach(sql::HAVING);
        orderByList.forEach(sql::ORDER_BY);
        return sql.toString();
    }

    public SelectBuilder or(Consumer<OrBuilder<SelectBuilder>> consumer) {
        return or(true, consumer);
    }

    public SelectBuilder or(boolean flag, Consumer<OrBuilder<SelectBuilder>> consumer) {
        if (flag) {
            orList.add(new OrBuilder<>(paramMap, consumer).build());
        }
        return this;
    }

    public SelectBuilder and(Consumer<AndBuilder<SelectBuilder>> consumer) {
        return and(true, consumer);
    }

    public SelectBuilder and(boolean flag, Consumer<AndBuilder<SelectBuilder>> consumer) {
        if (flag) {
            andList.add(new AndBuilder<>(paramMap, consumer).build());
        }
        return this;
    }

    @SafeVarargs
    public final <R> SelectBuilder select(SerializableFunction<R, ?>... functions) {
        for (SerializableFunction<R, ?> function : functions) {
            String columnAsColumn = FunctionUtil.getMetaColumn(function).wrapTableDotColumnAsColumn();
            selectList.add(columnAsColumn);
        }
        return this;
    }

    public <R> SelectBuilder select(SerializableFunction<R, ?> function, String alias) {
        String wrapped = FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
        selectList.add(StrUtil.format("{} AS {}", wrapped, alias));
        return this;
    }

    public SelectBuilder select(Class<?> rClass) {
        List<Field> fields = AnnoUtil.getFields(rClass, new ArrayList<>());
        for (Field field : fields) {
            if (field.isAnnotationPresent(SelectIgnore.class)) {
                continue;
            }
            selectList.add(MetaColumn.ofField(field).wrapTableDotColumnAsColumn());
        }
        return resultClass(rClass);
    }

    public SelectBuilder from(String table) {
        return from(table, null);
    }

    public SelectBuilder from(Class<?> tClass) {
        return from(tClass, null);
    }

    public SelectBuilder from(Class<?> tClass, String alias) {
        return from(AnnoUtil.getTableName(tClass), alias);
    }

    public SelectBuilder from(String table, String alias) {
        String aliasName = StrUtil.isNotEmpty(alias) ? alias : table;
        String tableWrapped = SqlConstant.wrapBackQuote(table);
        String aliasWrapped = SqlConstant.wrapBackQuote(aliasName);
        fromList.add(StrUtil.format("{} AS {}", tableWrapped, aliasWrapped));
        return this;
    }

    public SelectBuilder having(Consumer<HavingBuilder<SelectBuilder>> consumer) {
        return having(true, consumer);
    }

    public SelectBuilder having(boolean flag, Consumer<HavingBuilder<SelectBuilder>> consumer) {
        if (flag) {
            havingList.add(new HavingBuilder<>(paramMap, consumer).build());
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
            for (SerializableFunction<R, ?> function : functions) {
                String column = FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
                groupByList.add(column);
            }
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
            for (SerializableFunction<R, ?> function : functions) {
                String column = FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
                if (isAsc) {
                    orderByList.add(column + SqlConstant._ASC);
                } else {
                    orderByList.add(column + SqlConstant._DESC);
                }
            }
        }
        return this;
    }

    public SelectBuilder leftJoin(JoinBuilder<SelectBuilder> joinBuilder) {
        return join(true, JoinType.LEFT, joinBuilder);
    }

    public SelectBuilder rightJoin(JoinBuilder<SelectBuilder> joinBuilder) {
        return join(true, JoinType.RIGHT, joinBuilder);
    }

    public SelectBuilder innerJoin(JoinBuilder<SelectBuilder> joinBuilder) {
        return join(true, JoinType.INNER, joinBuilder);
    }

    public SelectBuilder outerJoin(JoinBuilder<SelectBuilder> joinBuilder) {
        return join(true, JoinType.OUTER, joinBuilder);
    }

    public SelectBuilder leftJoin(boolean flag, JoinBuilder<SelectBuilder> joinBuilder) {
        return join(flag, JoinType.LEFT, joinBuilder);
    }

    public SelectBuilder rightJoin(boolean flag, JoinBuilder<SelectBuilder> joinBuilder) {
        return join(flag, JoinType.RIGHT, joinBuilder);
    }

    public SelectBuilder innerJoin(boolean flag, JoinBuilder<SelectBuilder> joinBuilder) {
        return join(flag, JoinType.INNER, joinBuilder);
    }

    public SelectBuilder outerJoin(boolean flag, JoinBuilder<SelectBuilder> joinBuilder) {
        return join(flag, JoinType.OUTER, joinBuilder);
    }

    public <R> SelectBuilder leftJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return leftJoin(true, lFunc, rFunc, null);
    }

    public <R> SelectBuilder rightJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return rightJoin(true, lFunc, rFunc, null);
    }

    public <R> SelectBuilder innerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return innerJoin(true, lFunc, rFunc, null);
    }

    public <R> SelectBuilder outerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return outerJoin(true, lFunc, rFunc, null);
    }

    public <R> SelectBuilder leftJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder rightJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder innerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder outerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder leftJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.LEFT, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder rightJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.RIGHT, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder innerJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.INNER, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder outerJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.OUTER, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder join(JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return join(true, joinType, lFunc, rFunc, null);
    }

    public <R> SelectBuilder join(JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(true, joinType, lFunc, rFunc, consumer);
    }

    public <R> SelectBuilder join(boolean flag, JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, joinType, new JoinBuilder<>(this, lFunc, rFunc, paramMap, consumer));
    }

    public SelectBuilder join(boolean flag, JoinType joinType, JoinBuilder<SelectBuilder> joinBuilder) {
        if (flag) {
            String join = joinBuilder.build();
            switch (joinType) {
                case INNER:
                    innerJoinList.add(join);
                    break;
                case OUTER:
                    outerJoinList.add(join);
                    break;
                case LEFT:
                    leftOuterJoinList.add(join);
                    break;
                case RIGHT:
                    rightOuterJoinList.add(join);
                    break;
                default:
                    break;
            }
        }
        return this;
    }

    public <T> T one() {
        return (T) one(resultClass);
    }

    public <T> T one(Class<T> resultType) {
        return ObjectUtil.isNotEmpty(sql) ? SqlExecutor.queryOne(build(), paramMap, resultType) : null;
    }

    public <T> List<T> list() {
        return (List<T>) list(resultClass);
    }

    public <T> List<T> list(Class<T> resultType) {
        return ObjectUtil.isNotEmpty(sql) ? SqlExecutor.queryList(build(), paramMap, resultType) : null;
    }

}
