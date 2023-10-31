package com.huangxin.mybatis.type;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;
import com.huangxin.mybatis.func.ConditionFunction;

import java.util.*;
import java.util.stream.Collectors;

public enum ConditionType {
    //相等.
    EQ((column, param, paramMap) -> StrUtil.format("{} = {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //不等于.
    NE((column, param, paramMap) -> StrUtil.format("{} <> {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //大于.
    GT((column, param, paramMap) -> StrUtil.format("{} > {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //大于等于.
    GE((column, param, paramMap) -> StrUtil.format("{} >= {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //小于.
    LT((column, param, paramMap) -> StrUtil.format("{} < {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //小于等于.
    LE((column, param, paramMap) -> StrUtil.format("{} <= {}", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //中间模糊 %Value%.
    LIKE((column, param, paramMap) -> StrUtil.format("{} LIKE CONCAT('%',{},'%')", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //not like %Value%.
    NOT_LIKE((column, param, paramMap) -> StrUtil.format("{} NOT LIKE CONCAT('%',{},'%')", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //左模糊 %Value.
    LEFT_LIKE((column, param, paramMap) -> StrUtil.format("{} LIKE CONCAT('%',{})", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //右模糊 Value%.
    RIGHT_LIKE((column, param, paramMap) -> StrUtil.format("{} LIKE CONCAT({},'%')", column, SqlConstant.wrapParam(getAndPutNextKey(paramMap, param)))),

    //IN.(String类型用","隔开，集合调用的是toString方法)
    IN((column, param, paramMap) -> {
        if (param instanceof String) {
            List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
            return StrUtil.format("{} IN {}", column, assemIn(list, paramMap));
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            return StrUtil.format("{} IN {}", column, assemIn((Collection<?>) param, paramMap));
        }
        return null;
    }),

    //NOT IN.(String类型用","隔开，集合调用的是toString方法)
    NOT_IN((column, param, paramMap) -> {
        if (param instanceof String) {
            List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
            return StrUtil.format("{} NOT IN {}", column, assemIn(list, paramMap));
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            return StrUtil.format("{} NOT IN {}", column, assemIn((Collection<?>) param, paramMap));
        }
        return null;
    }),

    //在两个值之间.(String类型用","隔开，集合只取前两个)
    BETWEEN((column, param, paramMap) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(SqlConstant.COMMA);
            return StrUtil.format("{} BETWEEN {} AND {}",
                    column,
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, arg[0])),
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, arg[1])));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return StrUtil.format("{} BETWEEN {} AND {}",
                    column,
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, iterator.next())),
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, iterator.next())));
        }
        return null;
    }),

    //不在两个值之间.(String类型用","隔开，集合只取前两个)
    NOT_BETWEEN((column, param, paramMap) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(SqlConstant.COMMA);
            return StrUtil.format("{} NOT BETWEEN {} AND {}",
                    column,
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, arg[0])),
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, arg[1])));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return StrUtil.format("{} NOT BETWEEN {} AND {}",
                    column,
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, iterator.next())),
                    SqlConstant.wrapParam(getAndPutNextKey(paramMap, iterator.next())));
        }
        return null;
    }),

    //为空.
    IS_NULL((column, param, paramMap) -> StrUtil.format("{} IS NULL", column)),

    //不为空.
    IS_NOT_NULL((column, param, paramMap) -> StrUtil.format("{} IS NOT NULL", column));

    private final ConditionFunction<String, Object, Map<String, Object>, String> conditionFunction;

    ConditionType(ConditionFunction<String, Object, Map<String, Object>, String> conditionFunction) {
        this.conditionFunction = conditionFunction;
    }

    public static String resolve(ConditionType conditionType, String column, Object param, Map<String, Object> paramMap) {
        return conditionType.conditionFunction.apply(column, param, paramMap);
    }

    private static String assemIn(Collection<?> param, Map<String, Object> paramMap) {
        return param.stream()
                .map(arg -> SqlConstant.wrapParam(getAndPutNextKey(paramMap, arg)))
                .collect(Collectors.joining(SqlConstant.COMMA_, SqlConstant.PRE_BRACKET, SqlConstant.POST_BRACKET));
    }

    private static String getAndPutNextKey(Map<String, Object> paramMap, Object param) {
        String nextKey = SqlConstant.ARG + paramMap.size();
        paramMap.put(nextKey, param);
        return nextKey;
    }
}
