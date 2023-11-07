package com.huangxin.sql.util;

import com.huangxin.sql.builder.AbstractConditionBuilder;

/**
 * SelectUtil
 *
 * @author 黄鑫
 */
public class BuilderUtil {

    private static final ThreadLocal<AbstractConditionBuilder<?>> LOCAL = new ThreadLocal<>();

    public static <T extends AbstractConditionBuilder<T>> void set(AbstractConditionBuilder<T> builder) {
        LOCAL.set(builder);
    }

    public static void remove() {
        LOCAL.remove();
    }

    public static <T extends AbstractConditionBuilder<T>> T get() {
        return (T) LOCAL.get();
    }
}
