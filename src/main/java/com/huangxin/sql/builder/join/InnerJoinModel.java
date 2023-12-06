package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * innerJoin
 *
 * @author 黄鑫
 */
public interface InnerJoinModel<T> extends BaseJoinModel<T> {

    default T innerJoin( Class<?> joinClass) {
        return innerJoin(true, joinClass, null, null);
    }

    default T innerJoin(Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return innerJoin(true, joinClass, null, consumer);
    }

    default T innerJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return innerJoin(true, joinClass, joinAlias, consumer);
    }

    default T innerJoin(boolean flag, Class<?> joinClass) {
        return innerJoin(flag, joinClass, null, null);
    }

    default T innerJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return innerJoin(flag, joinClass, null, consumer);
    }

    default T innerJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.INNER, joinClass, joinAlias, consumer);
    }
}
