package com.huangxin.mybatis.func;

/**
 * ConditionFunction
 *
 * @author 黄鑫
 */
@FunctionalInterface
public interface ConditionFunction<COLUMN, PARAM, MAP, R> {

    R apply(COLUMN column, PARAM param, MAP paramMap);
}
