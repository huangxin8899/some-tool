package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * LeftJoin
 *
 * @author 黄鑫
 */
public interface LeftJoinModel<T> extends BaseJoinModel<T> {

    default T leftJoin(Class<?> joinClass) {
        return leftJoin(true, joinClass, null, null);
    }

    default T leftJoin(Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return leftJoin(true, joinClass, null, consumer);
    }

    default T leftJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return leftJoin(true, joinClass, joinAlias, consumer);
    }

    default T leftJoin(boolean flag, Class<?> joinClass) {
        return leftJoin(flag, joinClass, null, null);
    }

    default T leftJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return leftJoin(flag, joinClass, null, consumer);
    }

    default T leftJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.LEFT, joinClass, joinAlias, consumer);
    }

}
