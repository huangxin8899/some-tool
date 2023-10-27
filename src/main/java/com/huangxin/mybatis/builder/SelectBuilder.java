package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.ConditionType;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.SqlConstant;
import com.huangxin.mybatis.anno.SelectIgnore;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.JoinType;
import com.huangxin.mybatis.util.SerializableFunction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * SelectBuilder
 *
 * @author 黄鑫
 */
public class SelectBuilder extends AbstractConditionBuilder<SelectBuilder> {

    protected final List<String> selectList = new ArrayList<>();
    protected final List<String> fromList = new ArrayList<>();
    protected final List<String> innerJoinList = new ArrayList<>();
    protected final List<String> outerJoinList = new ArrayList<>();
    protected final List<String> leftOuterJoinList = new ArrayList<>();
    protected final List<String> rightOuterJoinList = new ArrayList<>();
    protected final List<String> orderByList = new ArrayList<>();
    protected final List<String> groupByList = new ArrayList<>();

    protected final List<String> havingList = new ArrayList<>();
    protected final List<List<String>> havingOrList = new ArrayList<>();
    protected Boolean isHaving = Boolean.FALSE;

    @Override
    public String build() {
        selectList.forEach(sql::SELECT);
        fromList.forEach(sql::FROM);
        whereList.forEach(sql::WHERE);
        innerJoinList.forEach(sql::INNER_JOIN);
        outerJoinList.forEach(sql::OUTER_JOIN);
        leftOuterJoinList.forEach(sql::LEFT_OUTER_JOIN);
        rightOuterJoinList.forEach(sql::RIGHT_OUTER_JOIN);
        orList.forEach(ors -> {
            if (!ors.isEmpty()) {
                sql.OR().WHERE(ors.toArray(new String[0]));
            }
        });
        groupByList.forEach(sql::GROUP_BY);
        havingList.forEach(sql::HAVING);
        havingOrList.forEach(havingOrs -> {
            if (!havingOrs.isEmpty()) {
                sql.OR().HAVING(havingOrs.toArray(new String[0]));
            }
        });
        orderByList.forEach(sql::ORDER_BY);
        return sql.toString();
    }

    @Override
    public SelectBuilder apply(boolean flag, String applySql, Object... params) {
        if (flag) {
            Optional.ofNullable(StrUtil.format(applySql, params)).ifPresent(whereList::add);
        }
        return this;
    }

    @Override
    public SelectBuilder apply(boolean flag, ConditionType conditionType, String column, Object param) {
        if (flag) {
            String resolve = ConditionType.resolve(conditionType, column, param, paramMap);
            if (isAnd) {
                if (isOr) {
                    Optional.ofNullable(resolve).ifPresent(str -> andMap.get("or").add(str));
                } else {
                    Optional.ofNullable(resolve).ifPresent(str -> andMap.get("and").add(str));
                }
                return this;
            }

            if (isOr && !isHaving) {
                List<String> list = orList.get(orList.size() - 1);
                Optional.ofNullable(resolve).ifPresent(list::add);
            } else if (isHaving && !isOr) {
                Optional.ofNullable(resolve).ifPresent(havingList::add);
            } else if (isHaving) {
                List<String> list = havingOrList.get(havingOrList.size() - 1);
                Optional.ofNullable(resolve).ifPresent(list::add);
            } else {
                Optional.ofNullable(resolve).ifPresent(whereList::add);
            }

        }
        return this;
    }

    @Override
    public SelectBuilder or(boolean flag, Consumer<SelectBuilder> consumer) {
        if (flag) {
            try {
                isOr = Boolean.TRUE;
                if (isHaving) {
                    havingOrList.add(new ArrayList<>());
                } else {
                    orList.add(new ArrayList<>());
                }
                consumer.accept(this);
            } finally {
                isOr = Boolean.FALSE;
            }
        }
        return this;
    }

    @Override
    public SelectBuilder and(boolean flag, Consumer<SelectBuilder> consumer) {
        if (flag) {
            try {
                isAnd = Boolean.TRUE;
                consumer.accept(this);
            } finally {
                String andStr = String.join(" AND ", andMap.get("and"));
                String orStr = String.join(" OR ", andMap.get("or"));
                String merge = StrUtil.format("({} OR {})", andStr, orStr);
                if (isOr && !isHaving) {
                    List<String> list = orList.get(orList.size() - 1);
                    Optional.of(merge).ifPresent(list::add);
                } else if (isHaving && !isOr) {
                    Optional.of(merge).ifPresent(havingList::add);
                } else if (isHaving) {
                    List<String> list = havingOrList.get(havingOrList.size() - 1);
                    Optional.of(merge).ifPresent(list::add);
                } else {
                    Optional.of(merge).ifPresent(whereList::add);
                }
                isAnd = Boolean.FALSE;
            }
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
        selectList.add(wrapped + SqlConstant._AS_ + alias);
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
        return this;
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
        fromList.add(tableWrapped + SqlConstant._AS_ + aliasWrapped);
        return this;
    }

    public SelectBuilder having(Consumer<SelectBuilder> consumer) {
        return having(true, consumer);
    }

    public SelectBuilder having(boolean flag, Consumer<SelectBuilder> consumer) {
        if (flag) {
            try {
                isHaving = Boolean.TRUE;
                consumer.accept(this);
            } finally {
                isHaving = Boolean.FALSE;
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
        return join(flag, joinType, new JoinBuilder<>(this, lFunc, rFunc, consumer));
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

}
