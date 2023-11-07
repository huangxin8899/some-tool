package com.huangxin.sql.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.entity.MetaColumn;
import com.huangxin.sql.util.AnnoUtil;
import com.huangxin.sql.util.FunctionUtil;
import com.huangxin.sql.func.SerializableFunction;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * JoinBuild
 *
 * @author 黄鑫
 */
public class JoinBuilder<T extends AbstractConditionBuilder<T>> extends CommonConditionBuilder<JoinBuilder<T>> {

    protected Class<?> joinClass;
    protected String joinAlias;
    protected final T selectBuilder;
    protected final Consumer<JoinBuilder<T>> consumer;
    protected final String joinTable;

    public JoinBuilder(T selectBuilder, Class<?> joinClass, Consumer<JoinBuilder<T>> consumer) {
        this(selectBuilder, joinClass, null, consumer);
    }

    public JoinBuilder(T selectBuilder, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<T>> consumer) {
        this(selectBuilder, AnnoUtil.getTableName(joinClass), joinAlias, consumer);
        this.joinClass = joinClass;
        aliasMap.putIfAbsent(joinClass, joinAlias);
    }

    public JoinBuilder(T selectBuilder, String joinTable, Consumer<JoinBuilder<T>> consumer) {
        this(selectBuilder, joinTable, null, consumer);
    }

    public JoinBuilder(T selectBuilder, String joinTable, String joinAlias, Consumer<JoinBuilder<T>> consumer) {
        this.selectBuilder = selectBuilder;
        this.paramMap = selectBuilder.getParamMap();
        this.aliasMap = selectBuilder.aliasMap;
        this.joinTable = joinTable;
        this.joinAlias = StrUtil.isNotEmpty(joinAlias) ? joinAlias : joinTable;
        this.consumer = consumer;
    }

    @Override
    public String build() {
        StringBuilder conditionSegment = new StringBuilder();
        Optional.ofNullable(consumer).ifPresent(c -> c.accept(this));
        String conditionStr = whereList.stream().collect(Collectors.joining(SqlConstant._AND_, SqlConstant.PRE_BRACKET, SqlConstant.POST_BRACKET));
        conditionSegment.append(conditionStr);
        orNestList.forEach(s -> conditionSegment.append(StrUtil.format(" OR ({})", String.join(SqlConstant._AND_, s))));
        String alias = StrUtil.isNotEmpty(joinAlias) ? joinAlias : aliasMap.getOrDefault(joinClass, joinAlias);
        return StrUtil.format("`{}` AS `{}` ON {}", joinTable, alias, conditionSegment.toString());
    }

    @Override
    public <R> String getColumn(SerializableFunction<R, ?> function) {
        MetaColumn metaColumn = FunctionUtil.getMetaColumn(function);
        if (metaColumn.getTableClass().equals(joinClass)) {
            return StrUtil.format("`{}`.`{}`", joinAlias, metaColumn.getColumnName());
        }
        return metaColumn.wrapTableDotColumn(aliasMap);
    }

}
