package com.huangxin.mybatis.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author 黄鑫
 * @description SerializableFunction
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

}
