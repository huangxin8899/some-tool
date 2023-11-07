package com.huangxin.sql.anno;

import com.huangxin.sql.type.ConditionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionFlag {

    /**
     * 字段名
     */
    String fieldName() default "";

    /**
     * 条件类型
     */
    ConditionType type() default ConditionType.EQ;

    boolean update() default false;
}
