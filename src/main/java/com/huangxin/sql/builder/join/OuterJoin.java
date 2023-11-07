package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * OuterJoin
 *
 * @author 黄鑫
 */
public interface OuterJoin extends Join {

    default SelectBuilder outerJoin( Class<?> joinClass) {
        return outerJoin(true, joinClass, null, null);
    }

    default SelectBuilder outerJoin(String joinTable) {
        return outerJoin(true, joinTable, null, null);
    }

    default SelectBuilder outerJoin(Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(true, joinClass, null, consumer);
    }

    default SelectBuilder outerJoin(String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(true, joinTable, null, consumer);
    }

    default SelectBuilder outerJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(true, joinClass, joinAlias, consumer);
    }

    default SelectBuilder outerJoin(String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(true, joinTable, joinAlias, consumer);
    }

    default SelectBuilder outerJoin(boolean flag, Class<?> joinClass) {
        return outerJoin(flag, joinClass, null, null);
    }

    default SelectBuilder outerJoin(boolean flag, String joinTable) {
        return outerJoin(flag, joinTable, null, null);
    }

    default SelectBuilder outerJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(flag, joinClass, null, consumer);
    }

    default SelectBuilder outerJoin(boolean flag, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return outerJoin(flag, joinTable, null, consumer);
    }

    default SelectBuilder outerJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.OUTER, joinClass, joinAlias, consumer);
    }

    default SelectBuilder outerJoin(boolean flag, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.OUTER, joinTable, joinAlias, consumer);
    }
}
