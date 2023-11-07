package com.huangxin.sql.builder.join;

import com.huangxin.sql.builder.JoinBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.type.JoinType;

import java.util.function.Consumer;

/**
 * RightJoin
 *
 * @author 黄鑫
 */
public interface RightJoin extends Join {

    default SelectBuilder rightJoin(Class<?> joinClass) {
        return rightJoin(true, joinClass, null, null);
    }

    default SelectBuilder rightJoin(String joinTable) {
        return rightJoin(true, joinTable, null, null);
    }

    default SelectBuilder rightJoin(Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(true, joinClass, null, consumer);
    }

    default SelectBuilder rightJoin(String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(true, joinTable, null, consumer);
    }

    default SelectBuilder rightJoin(Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(true, joinClass, joinAlias, consumer);
    }

    default SelectBuilder rightJoin(String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(true, joinTable, joinAlias, consumer);
    }

    default SelectBuilder rightJoin(boolean flag, Class<?> joinClass) {
        return rightJoin(flag, joinClass, null, null);
    }

    default SelectBuilder rightJoin(boolean flag, String joinTable) {
        return rightJoin(flag, joinTable, null, null);
    }

    default SelectBuilder rightJoin(boolean flag, Class<?> joinClass, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(flag, joinClass, null, consumer);
    }

    default SelectBuilder rightJoin(boolean flag, String joinTable, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return rightJoin(flag, joinTable, null, consumer);
    }

    default SelectBuilder rightJoin(boolean flag, Class<?> joinClass, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.RIGHT, joinClass, joinAlias, consumer);
    }

    default SelectBuilder rightJoin(boolean flag, String joinTable, String joinAlias, Consumer<JoinBuilder<SelectBuilder>> consumer) {
        return join(flag, JoinType.RIGHT, joinTable, joinAlias, consumer);
    }
}
