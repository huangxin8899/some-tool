package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * LeftJoin
 *
 * @author 黄鑫
 */
public interface LeftJoin extends Join {

    default SelectBuilder leftJoin( Class<?> joinClass) {
        return leftJoin(true, joinClass, null, null);
    }

    default SelectBuilder leftJoin(String joinTable) {
        return leftJoin(true, joinTable, null, null);
    }

    default SelectBuilder leftJoin(Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(true, joinClass, null, consumer);
    }

    default SelectBuilder leftJoin(String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(true, joinTable, null, consumer);
    }

    default SelectBuilder leftJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(true, joinClass, joinAlias, consumer);
    }

    default SelectBuilder leftJoin(String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(true, joinTable, joinAlias, consumer);
    }

    default SelectBuilder leftJoin(boolean flag, Class<?> joinClass) {
        return leftJoin(flag, joinClass, null, null);
    }

    default SelectBuilder leftJoin(boolean flag, String joinTable) {
        return leftJoin(flag, joinTable, null, null);
    }

    default SelectBuilder leftJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(flag, joinClass, null, consumer);
    }

    default SelectBuilder leftJoin(boolean flag, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return leftJoin(flag, joinTable, null, consumer);
    }

    default SelectBuilder leftJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.LEFT, joinClass, joinAlias, consumer);
    }

    default SelectBuilder leftJoin(boolean flag, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.LEFT, joinTable, joinAlias, consumer);
    }
}
