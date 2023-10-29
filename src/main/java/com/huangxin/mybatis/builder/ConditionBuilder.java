package com.huangxin.mybatis.builder;

import com.huangxin.mybatis.ConditionType;
import com.huangxin.mybatis.util.FunctionUtil;
import com.huangxin.mybatis.util.SerializableFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * ConditionBuilder
 *
 * @author 黄鑫
 */
public interface ConditionBuilder<T> extends Builder {

    default <R> String getColumn(SerializableFunction<R, ?> function) {
        return FunctionUtil.getMetaColumn(function).wrapTableDotColumn();
    }

    default T apply(String applySql, Object... params) {
        return this.apply(true, applySql, params);
}

    T apply(boolean flag, String applySql, Object... params);

    default <R> T apply(ConditionType conditionType, SerializableFunction<R, ?> function, Object param) {
        return apply(true, conditionType, getColumn(function), param);
    }

    default <R> T apply(boolean flag, ConditionType conditionType, SerializableFunction<R, ?> function, Object param) {
        return apply(flag, conditionType, getColumn(function), param);
    }

    default <R> T apply(ConditionType conditionType, String column, Object param) {
        return apply(true, conditionType, column, param);
    }

    T apply(boolean flag, ConditionType conditionType, String column, Object param);

//    default T or(Consumer<ConditionBuilder<T>> consumer) {
//        return this.or(true, consumer);
//    }
//
//    T or(boolean flag, Consumer<ConditionBuilder<T>> consumer);
//
//    default T and(Consumer<ConditionBuilder<T>> consumer) {
//        return this.and(true, consumer);
//    }
//
//    T and(boolean flag, Consumer<ConditionBuilder<T>> consumer);

    default <R> T eq(SerializableFunction<R, ?> function, Object param) {
        return this.eq(true, function, param);
    }

    default <R> T eq(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.EQ, function, param);
    }

    default T eq(String column, Object param) {
        return this.eq(true, column, param);
    }

    default T eq(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.EQ, column, param);
    }

    default <R> T ne(SerializableFunction<R, ?> function, Object param) {
        return this.ne(true, function, param);
    }

    default <R> T ne(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.NE, function, param);
    }

    default T ne(String column, Object param) {
        return this.ne(true, column, param);
    }

    default T ne(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.NE, column, param);
    }

    default <R> T gt(SerializableFunction<R, ?> function, Object param) {
        return this.gt(true, function, param);
    }

    default <R> T gt(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.GT, function, param);
    }

    default T gt(String column, Object param) {
        return this.gt(true, column, param);
    }

    default T gt(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.GT, column, param);
    }

    default <R> T ge(SerializableFunction<R, ?> function, Object param) {
        return this.ge(true, function, param);
    }

    default <R> T ge(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.GE, function, param);
    }

    default T ge(String column, Object param) {
        return this.ge(true, column, param);
    }

    default T ge(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.GE, column, param);
    }

    default <R> T lt(SerializableFunction<R, ?> function, Object param) {
        return this.lt(true, function, param);
    }

    default <R> T lt(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.LT, function, param);
    }

    default T lt(String column, Object param) {
        return this.lt(true, column, param);
    }

    default T lt(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.LT, column, param);
    }

    default <R> T le(SerializableFunction<R, ?> function, Object param) {
        return this.le(true, function, param);
    }

    default <R> T le(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.LE, function, param);
    }

    default T le(String column, Object param) {
        return this.le(true, column, param);
    }

    default T le(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.LE, column, param);
    }

    default <R> T like(SerializableFunction<R, ?> function, Object param) {
        return this.like(true, function, param);
    }

    default <R> T like(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.LIKE, function, param);
    }

    default T like(String column, Object param) {
        return this.like(true, column, param);
    }

    default T like(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.LIKE, column, param);
    }

    default <R> T notLike(SerializableFunction<R, ?> function, Object param) {
        return this.notLike(true, function, param);
    }

    default <R> T notLike(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.NOT_LIKE, function, param);
    }

    default T notLike(String column, Object param) {
        return this.notLike(true, column, param);
    }

    default T notLike(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.NOT_LIKE, column, param);
    }

    default <R> T leftLike(SerializableFunction<R, ?> function, Object param) {
        return this.leftLike(true, function, param);
    }

    default <R> T leftLike(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.LEFT_LIKE, function, param);
    }

    default T leftLike(String column, Object param) {
        return this.leftLike(true, column, param);
    }

    default T leftLike(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.LEFT_LIKE, column, param);
    }

    default <R> T rightLike(SerializableFunction<R, ?> function, Object param) {
        return this.rightLike(true, function, param);
    }

    default <R> T rightLike(boolean flag, SerializableFunction<R, ?> function, Object param) {
        return this.apply(flag, ConditionType.RIGHT_LIKE, function, param);
    }

    default T rightLike(String column, Object param) {
        return this.rightLike(true, column, param);
    }

    default T rightLike(boolean flag, String column, Object param) {
        return this.apply(flag, ConditionType.RIGHT_LIKE, column, param);
    }

    default <R> T in(SerializableFunction<R, ?> function, Object... params) {
        return this.in(true, function, params);
    }

    default <R> T in(SerializableFunction<R, ?> function, Collection<?> params) {
        return this.in(true, function, params);
    }

    default <R> T in(boolean flag, SerializableFunction<R, ?> function, Object... params) {
        List<Object> list = Arrays.asList(params);
        return this.apply(flag, ConditionType.IN, function, list);
    }

    default <R> T in(boolean flag, SerializableFunction<R, ?> function, Collection<?> param) {
        return this.apply(flag, ConditionType.IN, function, param);
    }

    default T in(String column, Object... params) {
        return this.in(true, column, params);
    }

    default T in(String column, Collection<?> param) {
        return this.in(true, column, param);
    }

    default T in(boolean flag, String column, Object... params) {
        List<Object> list = Arrays.asList(params);
        return this.apply(flag, ConditionType.IN, column, list);
    }


    default <R> T notIn(SerializableFunction<R, ?> function, Object... params) {
        return this.notIn(true, function, params);
    }

    default <R> T notIn(SerializableFunction<R, ?> function, Collection<?> params) {
        return this.notIn(true, function, params);
    }

    default <R> T notIn(boolean flag, SerializableFunction<R, ?> function, Object... params) {
        List<Object> list = Arrays.asList(params);
        return this.apply(flag, ConditionType.NOT_IN, function, list);
    }

    default <R> T notIn(boolean flag, SerializableFunction<R, ?> function, Collection<?> param) {
        return this.apply(flag, ConditionType.NOT_IN, function, param);
    }

    default T notIn(String column, Object... params) {
        return this.notIn(true, column, params);
    }

    default T notIn(String column, Collection<?> param) {
        return this.notIn(true, column, param);
    }

    default T notIn(boolean flag, String column, Object... params) {
        List<Object> list = Arrays.asList(params);
        return this.apply(flag, ConditionType.NOT_IN, column, list);
    }

    default <R> T between(SerializableFunction<R, ?> function, Object param1, Object param2) {
        return this.between(true, function, param1, param2);
    }

    default <R> T between(boolean flag, SerializableFunction<R, ?> function, Object param1, Object param2) {
        List<Object> params = Arrays.asList(param1, param2);
        return this.apply(flag, ConditionType.BETWEEN, function, params);
    }

    default T between(String column, Object param1, Object param2) {
        return this.between(true, column, param1, param2);
    }

    default T between(boolean flag, String column, Object param1, Object param2) {
        List<Object> params = Arrays.asList(param1, param2);
        return this.apply(flag, ConditionType.BETWEEN, column, params);
    }

    default <R> T notBetween(SerializableFunction<R, ?> function, Object param1, Object param2) {
        return this.notBetween(true, function, param1, param2);
    }

    default <R> T notBetween(boolean flag, SerializableFunction<R, ?> function, Object param1, Object param2) {
        List<Object> params = Arrays.asList(param1, param2);
        return this.apply(flag, ConditionType.NOT_BETWEEN, function, params);
    }

    default T notBetween(String column, Object param1, Object param2) {
        return this.notBetween(true, column, param1, param2);
    }

    default T notBetween(boolean flag, String column, Object param1, Object param2) {
        List<Object> params = Arrays.asList(param1, param2);
        return this.apply(flag, ConditionType.NOT_BETWEEN, column, params);
    }

    default <R> T isNull(SerializableFunction<R, ?> function) {
        return this.isNull(true, function);
    }

    default <R> T isNull(boolean flag, SerializableFunction<R, ?> function) {
        return this.apply(flag, ConditionType.IS_NULL, function, null);
    }

    default T isNull(String column) {
        return this.isNull(true, column);
    }

    default T isNull(boolean flag, String column) {
        return this.apply(flag, ConditionType.IS_NULL, column, null);
    }

    default <R> T isNotNull(SerializableFunction<R, ?> function) {
        return this.isNotNull(true, function);
    }

    default <R> T isNotNull(boolean flag, SerializableFunction<R, ?> function) {
        return this.apply(flag, ConditionType.IS_NOT_NULL, function, null);
    }

    default T isNotNull(String column) {
        return this.isNotNull(true, column);
    }

    default T isNotNull(boolean flag, String column) {
        return this.apply(flag, ConditionType.IS_NOT_NULL, column, null);
    }
}
