package com.huangxin.dict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 缓存key
     */
    String key() default "";

    /**
     * 是否从缓存获取
     */
    boolean cache() default true;

    /**
     * 字典code
     */
    String code() default "";

    /**
     * 静态翻译
     * 通过“code_value”的方式翻译
     * 例如：“0_成功”，“1_失败”
     */
    String[] replace() default {};

    /**
     * 翻译结果赋值到指定字段
     * 若无此字段，则会新增字段并赋值
     */
    String fieldName() default "";

    /**
     * 后缀
     */
    String suffix() default "Value";


}
