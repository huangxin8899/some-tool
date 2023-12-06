package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * OuterJoin
 *
 * @author 黄鑫
 */
public interface OuterJoinModel<T> extends BaseJoinModel<T> {

    default T outerJoin(Class<?> joinClass) {
        return outerJoin(true, joinClass, null, null);
    }

    default T outerJoin(Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return outerJoin(true, joinClass, null, consumer);
    }

    default T outerJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return outerJoin(true, joinClass, joinAlias, consumer);
    }

    default T outerJoin(boolean flag, Class<?> joinClass) {
        return outerJoin(flag, joinClass, null, null);
    }

    default T outerJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return outerJoin(flag, joinClass, null, consumer);
    }

    default T outerJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.OUTER, joinClass, joinAlias, consumer);
    }
}
