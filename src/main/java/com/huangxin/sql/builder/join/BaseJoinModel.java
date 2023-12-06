package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * Join
 *
 * @author 黄鑫
 */
public interface BaseJoinModel<T> {

    default T join(JoinType joinType, Class<?> joinClass) {
        return join(true, joinType, joinClass, null);
    }

    default T join(JoinType joinType, Class<?> joinClass, String joinAlias) {
        return join(true, joinType, joinClass, joinAlias, null);
    }

    default T join(JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return join(true, joinType, joinClass, consumer);
    }

    default T join(JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return join(true, joinType, joinClass, joinAlias, consumer);
    }

    default T join(boolean flag, JoinType joinType, Class<?> joinClass) {
        return join(flag, joinType, joinClass, null, null);
    }

    default T join(boolean flag, JoinType joinType, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return join(flag, joinType, joinClass, null, consumer);
    }

    T join(boolean flag, JoinType joinType, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer);

}
