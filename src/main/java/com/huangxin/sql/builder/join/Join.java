package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * Join
 *
 * @author 黄鑫
 */
public interface Join {

    default SelectBuilder join(JoinType joinType, Class<?> joinClass) {
        return join(true, joinType, joinClass, null);
    }

    default SelectBuilder join(JoinType joinType, String joinTable) {
        return join(true, joinType, joinTable, null);
    }

    default SelectBuilder join(JoinType joinType, Class<?> joinClass, String joinAlias) {
        return join(true, joinType, joinClass, joinAlias, null);
    }

    default SelectBuilder join(JoinType joinType, String joinTable, String joinAlias) {
        return join(true, joinType, joinTable, joinAlias, null);
    }

    default SelectBuilder join(JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(true, joinType, joinClass, consumer);
    }

    default SelectBuilder join(JoinType joinType, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(true, joinType, joinTable, consumer);
    }

    default SelectBuilder join(JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(true, joinType, joinClass, joinAlias,  consumer);
    }

    default SelectBuilder join(JoinType joinType, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(true, joinType, joinTable, joinAlias,  consumer);
    }
    
    SelectBuilder join(boolean flag, JoinType joinType, JoinBuilder<SelectBuilder> joinBuilder);

    SelectBuilder join(boolean flag, JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer);

    SelectBuilder join(boolean flag, JoinType joinType, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer);

    SelectBuilder join(boolean flag, JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer);

    SelectBuilder join(boolean flag, JoinType joinType, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer);
}
