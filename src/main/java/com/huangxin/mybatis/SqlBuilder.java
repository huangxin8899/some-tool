package com.huangxin.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.anno.ResultIgnore;
import com.huangxin.mybatis.util.AnnoUtil;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.JoinType;
import com.huangxin.mybatis.util.SerializableFunction;
import lombok.Getter;
import org.apache.ibatis.jdbc.AbstractSQL;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * SqlBuilder
 *
 * @author 黄鑫
 */
public class SqlBuilder extends AbstractSQL<SqlBuilder> implements ConditionBuilder<SqlBuilder> {

    private final List<String> selectList = new ArrayList<>();
    private final List<String> fromList = new ArrayList<>();
    private final List<String> innerJoinList = new ArrayList<>();
    private final List<String> outerJoinList = new ArrayList<>();
    private final List<String> leftOuterJoinList = new ArrayList<>();
    private final List<String> rightOuterJoinList = new ArrayList<>();
    private final List<String> orderByList = new ArrayList<>();
    private final List<String> groupByList = new ArrayList<>();
    private final List<String> havingList = new ArrayList<>();

    protected final List<String> whereList = new ArrayList<>();
    protected final List<List<String>> orList = new ArrayList<>();
    protected final List<List<String>> havingOrList = new ArrayList<>();
    protected Boolean isOr = Boolean.FALSE;
    protected Boolean isHaving = Boolean.FALSE;

    @Getter
    protected Map<String, Object> paramMap = new HashMap<>();
    @Getter
    protected Object mateObj;

    @Override
    public String build() {
        if (!selectList.isEmpty()) {
            SELECT(selectList.toArray(new String[0]));
        }
        if (!fromList.isEmpty()) {
            FROM(fromList.toArray(new String[0]));
        }
        if (!whereList.isEmpty()) {
            WHERE(whereList.toArray(new String[0]));
        }
        if (!innerJoinList.isEmpty()) {
            INNER_JOIN(innerJoinList.toArray(new String[0]));
        }
        if (!outerJoinList.isEmpty()) {
            OUTER_JOIN(outerJoinList.toArray(new String[0]));
        }
        if (!leftOuterJoinList.isEmpty()) {
            LEFT_OUTER_JOIN(leftOuterJoinList.toArray(new String[0]));
        }
        if (!rightOuterJoinList.isEmpty()) {
            RIGHT_OUTER_JOIN(rightOuterJoinList.toArray(new String[0]));
        }
        if (!orderByList.isEmpty()) {
            ORDER_BY(orderByList.toArray(new String[0]));
        }
        if (!groupByList.isEmpty()) {
            GROUP_BY(groupByList.toArray(new String[0]));
        }
        orList.forEach(strings -> {
            if (!strings.isEmpty()) {
                OR().WHERE(strings.toArray(new String[0]));
            }
        });
        if (!havingList.isEmpty()) {
            HAVING(havingList.toArray(new String[0]));
        }
        havingOrList.forEach(strings -> {
            if (!strings.isEmpty()) {
                OR().HAVING(strings.toArray(new String[0]));
            }
        });

        return toString();
    }

    @Override
    public SqlBuilder getSelf() {
        return this;
    }

    @Override
    public SqlBuilder apply(boolean flag, String applySql, Object... params) {
        if (flag) {
            Optional.ofNullable(StrUtil.format(applySql, params)).ifPresent(whereList::add);
        }
        return this;
    }

    @Override
    public SqlBuilder apply(boolean flag, ConditionType conditionType, String column, Object param) {
        if (flag) {
            String resolve = ConditionType.resolve(conditionType, column, param, paramMap);
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
    public SqlBuilder or(boolean flag, Consumer<SqlBuilder> consumer) {
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

    @SafeVarargs
    public final <R> SqlBuilder select(SerializableFunction<R, ?>... functions) {
        for (SerializableFunction<R, ?> function : functions) {
            String columnAsColumn = FunctionUtil.getMetaColumn(function).wrapTableDotColumnAsColumn();
            selectList.add(columnAsColumn);
        }
        return this;
    }

    public <R> SqlBuilder select(SerializableFunction<R, ?> function, String alias) {
        String wrapped = FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
        selectList.add(wrapped + SqlConstant._AS_ + alias);
        return this;
    }

    public SqlBuilder select(Class<?> rClass) {
        List<Field> fields = AnnoUtil.getFields(rClass, new ArrayList<>());
        for (Field field : fields) {
            if (field.isAnnotationPresent(ResultIgnore.class)) {
                continue;
            }
            selectList.add(MetaColumn.ofField(field).wrapTableDotColumnAsColumn());
        }
        return this;
    }

    public SqlBuilder from(Class<?> tClass) {
        return from(tClass, null);
    }

    public SqlBuilder from(Class<?> tClass, String alias) {
        String wrapped;
        String tableName = SqlConstant.wrapBackQuote(AnnoUtil.getTableName(tClass));
        if (StrUtil.isNotEmpty(alias)) {
            wrapped = SqlConstant.wrapBackQuote(alias);
        } else {
            wrapped = tableName;
        }
        fromList.add(tableName + SqlConstant._AS_ + wrapped);
        return this;
    }

    public SqlBuilder having(Consumer<SqlBuilder> consumer) {
        return having(true, consumer);
    }

    public SqlBuilder having(boolean flag, Consumer<SqlBuilder> consumer) {
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
    public final <R> SqlBuilder groupBy(SerializableFunction<R, ?>... functions) {
        return groupBy(true, functions);
    }

    @SafeVarargs
    public final <R> SqlBuilder groupBy(boolean flag, SerializableFunction<R, ?>... functions) {
        if (flag) {
            for (SerializableFunction<R, ?> function : functions) {
                String column = FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
                groupByList.add(column);
            }
        }
        return this;
    }

    @SafeVarargs
    public final <R> SqlBuilder orderByAsc(SerializableFunction<R, ?>... functions) {
        return orderByAsc(true, functions);
    }

    @SafeVarargs
    public final <R> SqlBuilder orderByAsc(boolean flag, SerializableFunction<R, ?>... functions) {
        return orderBy(flag, true, functions);
    }

    @SafeVarargs
    public final <R> SqlBuilder orderByDesc(SerializableFunction<R, ?>... functions) {
        return orderByDesc(true, functions);
    }

    @SafeVarargs
    public final <R> SqlBuilder orderByDesc(boolean flag, SerializableFunction<R, ?>... functions) {
        return orderBy(flag, false, functions);
    }

    @SafeVarargs
    public final <R> SqlBuilder orderBy(boolean flag, boolean isAsc, SerializableFunction<R, ?>... functions) {
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

    public SqlBuilder leftJoin(JoinBuilder joinBuilder) {
        return join(true, JoinType.LEFT, joinBuilder);
    }

    public SqlBuilder rightJoin(JoinBuilder joinBuilder) {
        return join(true, JoinType.RIGHT, joinBuilder);
    }

    public SqlBuilder innerJoin(JoinBuilder joinBuilder) {
        return join(true, JoinType.INNER, joinBuilder);
    }

    public SqlBuilder outerJoin(JoinBuilder joinBuilder) {
        return join(true, JoinType.OUTER, joinBuilder);
    }

    public SqlBuilder leftJoin(boolean flag, JoinBuilder joinBuilder) {
        return join(flag, JoinType.LEFT, joinBuilder);
    }

    public SqlBuilder rightJoin(boolean flag, JoinBuilder joinBuilder) {
        return join(flag, JoinType.RIGHT, joinBuilder);
    }

    public SqlBuilder innerJoin(boolean flag, JoinBuilder joinBuilder) {
        return join(flag, JoinType.INNER, joinBuilder);
    }

    public SqlBuilder outerJoin(boolean flag, JoinBuilder joinBuilder) {
        return join(flag, JoinType.OUTER, joinBuilder);
    }

    public <R> SqlBuilder leftJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return leftJoin(true, lFunc, rFunc, null);
    }

    public <R> SqlBuilder rightJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return rightJoin(true, lFunc, rFunc, null);
    }

    public <R> SqlBuilder innerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return innerJoin(true, lFunc, rFunc, null);
    }

    public <R> SqlBuilder outerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return outerJoin(true, lFunc, rFunc, null);
    }

    public <R> SqlBuilder leftJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return leftJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder rightJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return rightJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder innerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return innerJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder outerJoin(SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return outerJoin(true, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder leftJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.LEFT, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder rightJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.RIGHT, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder innerJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.INNER, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder outerJoin(boolean flag, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.OUTER, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder join(JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc) {
        return join(true, joinType, lFunc, rFunc, null);
    }

    public <R> SqlBuilder join(JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(true, joinType, lFunc, rFunc, consumer);
    }

    public <R> SqlBuilder join(boolean flag, JoinType joinType, SerializableFunction<R, ?> lFunc, SerializableFunction<R, ?> rFunc, Consumer<JoinBuilder> consumer) {
        return join(flag, joinType, new JoinBuilder(this, lFunc, rFunc, consumer));
    }

    public SqlBuilder join(boolean flag, JoinType joinType, JoinBuilder joinBuilder) {
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

    @SafeVarargs
    public final <R> SqlBuilder insert(R r, Predicate<R>... filters) {
        return insertBatch(Collections.singletonList(r), filters);
    }

    @SafeVarargs
    public final <R> SqlBuilder insertBatch(Collection<R> collection, Predicate<R>... filters) {
        if (collection.isEmpty()) {
            return this;
        }

        R firstItem = collection.iterator().next();
        String tableName = AnnoUtil.getTableName(firstItem.getClass());
        Field[] fields = ReflectUtil.getFields(firstItem.getClass(), field -> !Modifier.isStatic(field.getModifiers()));
        boolean firstItemProcessed = false;

        for (R item : collection) {
            if (!firstItemProcessed) {
                INSERT_INTO(tableName);
                for (Field field : fields) {
                    String fieldName = ReflectUtil.getFieldName(field);
                    String underlineCase = StrUtil.toUnderlineCase(fieldName);

                    if (Arrays.stream(filters).noneMatch(filter -> filter.test(item))) {
                        INTO_COLUMNS(underlineCase);
                        Object fieldValue = ReflectUtil.getFieldValue(item, fieldName);
                        String paramName = SqlConstant.ARG + paramMap.size();
                        INTO_VALUES(StrUtil.format("'{}'", SqlConstant.wrapParam(paramName)));
                        paramMap.put(paramName, fieldValue);
                    }
                }
                firstItemProcessed = true;
            } else {
                ADD_ROW();
                for (Field field : fields) {
                    String fieldName = ReflectUtil.getFieldName(field);
                    Object fieldValue = ReflectUtil.getFieldValue(item, fieldName);
                    String paramName = SqlConstant.ARG + paramMap.size();
                    INTO_VALUES(StrUtil.format("'{}'", SqlConstant.wrapParam(paramName)));
                    paramMap.put(paramName, fieldValue);
                }
            }
        }
        return this;


    }

}
