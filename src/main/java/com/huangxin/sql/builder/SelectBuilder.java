package com.huangxin.sql.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.anno.SelectColumn;
import com.huangxin.sql.anno.SelectIgnore;
import com.huangxin.sql.builder.join.InnerJoin;
import com.huangxin.sql.builder.join.LeftJoin;
import com.huangxin.sql.builder.join.OuterJoin;
import com.huangxin.sql.builder.join.RightJoin;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.entity.MetaColumn;
import com.huangxin.sql.entity.Page;
import com.huangxin.sql.func.SelectFunc;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.type.JoinType;
import com.huangxin.sql.util.AnnoUtil;
import com.huangxin.sql.util.FunctionUtil;
import com.huangxin.sql.util.SqlSessionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SelectBuilder
 *
 * @author 黄鑫
 */
public class SelectBuilder
        extends AbstractConditionBuilder<SelectBuilder>
        implements LeftJoin, RightJoin, InnerJoin, OuterJoin {

    protected final List<String> selectList = new ArrayList<>();
    protected final List<SelectFunc> selectFuncList = new ArrayList<>();
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
    protected Long[] limit = {BuilderConfig.DEFAULT_LIMIT, null};
    protected Long offset;

    @Override
    public String build() {
        selectFuncList.forEach(SelectFunc::exec);
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
        String limitStr = Arrays.stream(limit)
                .filter(ObjectUtil::isNotEmpty)
                .filter(l -> l >= 0)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        sql.LIMIT(StrUtil.isNotEmpty(limitStr) ? limitStr : null);
        sql.OFFSET(ObjectUtil.isNotEmpty(offset) ? String.valueOf(offset) : null);
        return sql.toString();
    }

    public SelectBuilder or(Consumer<OrBuilder<SelectBuilder>> consumer) {
        return or(true, consumer);
    }

    public SelectBuilder or(boolean flag, Consumer<OrBuilder<SelectBuilder>> consumer) {
        if (flag) {
            orList.add(new OrBuilder<>(paramMap, aliasMap, consumer).build());
        }
        return this;
    }

    public SelectBuilder and(Consumer<AndBuilder<SelectBuilder>> consumer) {
        return and(true, consumer);
    }

    public SelectBuilder and(boolean flag, Consumer<AndBuilder<SelectBuilder>> consumer) {
        if (flag) {
            andList.add(new AndBuilder<>(paramMap, aliasMap, consumer).build());
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

    public SelectBuilder select(String selectStr) {
        selectList.add(selectStr);
        return this;
    }

    public SelectBuilder select(String... selects) {
        selectList.addAll(Arrays.asList(selects));
        return this;
    }

    public <R> SelectBuilder select(SerializableFunction<R, ?> function, String alias) {
        selectFuncList.add(() -> {
            String wrapped = FunctionUtil.getMetaColumn(function).wrapTableDotColumn(aliasMap);
            selectList.add(StrUtil.format("{} AS {}", wrapped, alias));
        });
        return this;
    }

    public SelectBuilder select(Class<?> rClass) {
        selectFuncList.add(() -> {
            List<Field> fields = AnnoUtil.getFields(rClass, new ArrayList<>());
            String table = null;
            if (rClass.isAnnotationPresent(SelectColumn.class)) {
                Class<?> tableClass = rClass.getAnnotation(SelectColumn.class).table();
                if (!tableClass.isAssignableFrom(Void.class)) {
                    table = aliasMap.getOrDefault(tableClass, AnnoUtil.getTableName(tableClass));
                }
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(SelectIgnore.class) ||
                        (!rClass.isAnnotationPresent(SelectColumn.class) && !field.isAnnotationPresent(SelectColumn.class))) {
                    continue;
                }
                if (field.isAnnotationPresent(SelectColumn.class)) {
                    SelectColumn selectColumn = field.getAnnotation(SelectColumn.class);
                    if (StrUtil.isNotEmpty(selectColumn.value())) {
                        selectList.add(selectColumn.value());
                        continue;
                    }
                    if (!selectColumn.table().isAssignableFrom(Void.class)) {
                        table = aliasMap.getOrDefault(selectColumn.table(), AnnoUtil.getTableName(selectColumn.table()));
                    }
                }
                String column = StrUtil.toUnderlineCase(field.getName());
                selectList.add(StrUtil.format("{}.{}", table, column));
            }
            resultClass(rClass);
        });
        return this;
    }

    public SelectBuilder from(String table) {
        return from(table, null);
    }

    public SelectBuilder from(Class<?> tClass) {
        return from(tClass, null);
    }

    public SelectBuilder from(Class<?> tClass, String alias) {
        String tableName = AnnoUtil.getTableName(tClass);
        String aliasName = StrUtil.isNotEmpty(alias) ? alias : tableName;
        aliasMap.put(tClass, aliasName);
        return from(tableName, aliasName);
    }

    public SelectBuilder from(String table, String alias) {
        String aliasName = StrUtil.isNotEmpty(alias) ? alias : table;
        fromList.add(StrUtil.format("`{}` AS `{}`", table, aliasName));
        return this;
    }

    public SelectBuilder having(Consumer<HavingBuilder<SelectBuilder>> consumer) {
        return having(true, consumer);
    }

    public SelectBuilder having(boolean flag, Consumer<HavingBuilder<SelectBuilder>> consumer) {
        if (flag) {
            havingList.add(new HavingBuilder<>(paramMap, aliasMap, consumer).build());
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
                String column = FunctionUtil.getMetaColumn(function).wrapTableDotColumn(aliasMap);
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
                String column = FunctionUtil.getMetaColumn(function).wrapTableDotColumn(aliasMap);
                if (isAsc) {
                    orderByList.add(column + SqlConstant._ASC);
                } else {
                    orderByList.add(column + SqlConstant._DESC);
                }
            }
        }
        return this;
    }

    @Override
    public SelectBuilder join(boolean flag, JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, joinType, new JoinBuilder<>(this, joinClass, consumer));
    }

    @Override
    public SelectBuilder join(boolean flag, JoinType joinType, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, joinType, new JoinBuilder<>(this, joinTable, consumer));
    }

    @Override
    public SelectBuilder join(boolean flag, JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, joinType, new JoinBuilder<>(this, joinClass, joinAlias, consumer));
    }

    @Override
    public SelectBuilder join(boolean flag, JoinType joinType, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, joinType, new JoinBuilder<>(this, joinTable, joinAlias, consumer));
    }

    @Override
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

    public SelectBuilder limit(long limit) {
        this.limit[0] = limit;
        return this;
    }

    public SelectBuilder limit(long limit1, long limit2) {
        limit[0] = limit1;
        limit[1] = limit2;
        return this;
    }

    public SelectBuilder offset(long offset) {
        this.offset = offset;
        return this;
    }

    public <T> T one() {
        return one(null);
    }

    public <T> T one(Class<T> resultType) {
        resultType = resultType != null ? resultType : (Class<T>) resultClass;
        String sql = build();
        return SqlSessionUtil.queryOne(sql, paramMap, resultType);
    }

    public <T> List<T> list() {
        return list(null);
    }

    public <T> List<T> list(Class<T> resultType) {
        return list(resultType, -1);
    }

    public <T> List<T> list(long size) {
        return list(null, size);
    }

    public <T> List<T> list(Class<T> resultType, long size) {
        resultType = resultType != null ? resultType : (Class<T>) resultClass;
        String sql = size >= 0 ? limit(size).build() : build();
        return SqlSessionUtil.queryList(sql, paramMap, resultType);
    }

    public int count() {
        selectList.clear();
        selectFuncList.clear();
        limit[0] = -1L;
        offset = null;
        String sql = select("COUNT(*)").build();
        Integer count = SqlSessionUtil.queryOne(sql, paramMap, Integer.class);
        return count != null ? count : 0;
    }

    public <T> Page<T> page() {
        return page(new Page<>());
    }

    public <T> Page<T> page(long current, long size) {
        return page(new Page<>(current, size));
    }

    public <T> Page<T> page(Page<T> page) {
        int count = ObjectUtil.clone(this).count();
        page.setTotal(count);
        limit(page.getCurrent() * page.getSize(), page.getSize());
        page.setRecords(list());
        return page;
    }

}
