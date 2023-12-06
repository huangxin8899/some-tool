package com.huangxin.sql.type;

import net.sf.jsqlparser.statement.select.Join;

import java.util.function.Function;

public enum JoinType {

    INNER(join -> join.withInner(true)),
    OUTER(join -> join.withOuter(true)),
    LEFT(join -> join.withLeft(true)),
    RIGHT(join -> join.withRight(true)),
    ;

    private final Function<Join, Join> function;

    JoinType(Function<Join, Join> function) {
        this.function = function;
    }

    public Join exec(Join join) {
        return function.apply(join);
    }
}
