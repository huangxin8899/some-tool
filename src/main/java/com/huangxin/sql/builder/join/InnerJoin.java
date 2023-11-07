package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * innerJoin
 *
 * @author 黄鑫
 */
public interface InnerJoin extends Join {

    default SelectBuilder innerJoin( Class<?> joinClass) {
        return innerJoin(true, joinClass, null, null);
    }

    default SelectBuilder innerJoin(String joinTable) {
        return innerJoin(true, joinTable, null, null);
    }

    default SelectBuilder innerJoin(Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(true, joinClass, null, consumer);
    }

    default SelectBuilder innerJoin(String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(true, joinTable, null, consumer);
    }

    default SelectBuilder innerJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(true, joinClass, joinAlias, consumer);
    }

    default SelectBuilder innerJoin(String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(true, joinTable, joinAlias, consumer);
    }

    default SelectBuilder innerJoin(boolean flag, Class<?> joinClass) {
        return innerJoin(flag, joinClass, null, null);
    }

    default SelectBuilder innerJoin(boolean flag, String joinTable) {
        return innerJoin(flag, joinTable, null, null);
    }

    default SelectBuilder innerJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(flag, joinClass, null, consumer);
    }

    default SelectBuilder innerJoin(boolean flag, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return innerJoin(flag, joinTable, null, consumer);
    }

    default SelectBuilder innerJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.INNER, joinClass, joinAlias, consumer);
    }

    default SelectBuilder innerJoin(boolean flag, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.INNER, joinTable, joinAlias, consumer);
    }
}
