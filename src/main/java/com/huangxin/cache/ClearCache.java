package com.huangxin.cache;

import java.lang.annotation.*;

/**
 *延时双删
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface ClearCache {

    String key() default "";

    long time() default 1000;
}
