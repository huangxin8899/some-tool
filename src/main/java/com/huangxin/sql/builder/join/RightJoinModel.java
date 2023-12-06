package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * RightJoin
 *
 * @author 黄鑫
 */
public interface RightJoinModel<T> extends BaseJoinModel<T> {

    default T rightJoin(Class<?> joinClass) {
        return rightJoin(true, joinClass, null, null);
    }

    default T rightJoin(Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return rightJoin(true, joinClass, null, consumer);
    }

    default T rightJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return rightJoin(true, joinClass, joinAlias, consumer);
    }

    default T rightJoin(boolean flag, Class<?> joinClass) {
        return rightJoin(flag, joinClass, null, null);
    }

    default T rightJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder> consumer) {
        return rightJoin(flag, joinClass, null, consumer);
    }

    default T rightJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder> consumer) {
        return join(flag, JoinType.RIGHT, joinClass, joinAlias, consumer);
    }
}
