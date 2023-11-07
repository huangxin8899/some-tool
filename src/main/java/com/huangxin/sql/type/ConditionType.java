package com.huangxin.sql.type;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.builder.AbstractConditionBuilder;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.func.ConditionFunction;

import java.util.*;
import java.util.stream.Collectors;

import static com.huangxin.sql.type.WrapType.getWrapSegment;

public enum ConditionType {
    //相等.
    EQ((column, param, builder) -> StrUtil.format("{} = {}", column, getWrapSegment(builder, param))),

    //不等于.
    NE((column, param, builder) -> StrUtil.format("{} <> {}", column, getWrapSegment(builder, param))),

    //大于.
    GT((column, param, builder) -> StrUtil.format("{} > {}", column, getWrapSegment(builder, param))),

    //大于等于.
    GE((column, param, builder) -> StrUtil.format("{} >= {}", column, getWrapSegment(builder, param))),

    //小于.
    LT((column, param, builder) -> StrUtil.format("{} < {}", column, getWrapSegment(builder, param))),

    //小于等于.
    LE((column, param, builder) -> StrUtil.format("{} <= {}", column, getWrapSegment(builder, param))),

    //中间模糊 %Value%.
    LIKE((column, param, builder) -> StrUtil.format("{} LIKE CONCAT('%',{},'%')", column, getWrapSegment(builder, param))),

    //not like %Value%.
    NOT_LIKE((column, param, builder) -> StrUtil.format("{} NOT LIKE CONCAT('%',{},'%')", column, getWrapSegment(builder, param))),

    //左模糊 %Value.
    LEFT_LIKE((column, param, builder) -> StrUtil.format("{} LIKE CONCAT('%',{})", column, getWrapSegment(builder, param))),

    //右模糊 Value%.
    RIGHT_LIKE((column, param, builder) -> StrUtil.format("{} LIKE CONCAT({},'%')", column, getWrapSegment(builder, param))),

    //IN.(String类型用","隔开，集合调用的是toString方法)
    IN((column, param, builder) -> {
        if (param instanceof String) {
            List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
            return StrUtil.format("{} IN {}", column, assemIn(list, builder));
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            return StrUtil.format("{} IN {}", column, assemIn((Collection<?>) param, builder));
        }
        return null;
    }),

    //NOT IN.(String类型用","隔开，集合调用的是toString方法)
    NOT_IN((column, param, builder) -> {
        if (param instanceof String) {
            List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
            return StrUtil.format("{} NOT IN {}", column, assemIn(list, builder));
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            return StrUtil.format("{} NOT IN {}", column, assemIn((Collection<?>) param, builder));
        }
        return null;
    }),

    //在两个值之间.(String类型用","隔开，集合只取前两个)
    BETWEEN((column, param, builder) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(SqlConstant.COMMA);
            return StrUtil.format("{} BETWEEN {} AND {}",
                    column,
                    getWrapSegment(builder, arg[0]),
                    getWrapSegment(builder, arg[1]));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return StrUtil.format("{} BETWEEN {} AND {}",
                    column,
                    getWrapSegment(builder, iterator.next()),
                    getWrapSegment(builder, iterator.next()));
        }
        return null;
    }),

    //不在两个值之间.(String类型用","隔开，集合只取前两个)
    NOT_BETWEEN((column, param, builder) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(SqlConstant.COMMA);
            return StrUtil.format("{} NOT BETWEEN {} AND {}",
                    column,
                    getWrapSegment(builder, arg[0]),
                    getWrapSegment(builder, arg[1]));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return StrUtil.format("{} NOT BETWEEN {} AND {}",
                    column,
                    getWrapSegment(builder, iterator.next()),
                    getWrapSegment(builder, iterator.next()));
        }
        return null;
    }),

    //为空.
    IS_NULL((column, param, builder) -> StrUtil.format("{} IS NULL", column)),

    //不为空.
    IS_NOT_NULL((column, param, builder) -> StrUtil.format("{} IS NOT NULL", column));

    private final ConditionFunction<String, Object, AbstractConditionBuilder<?>, String> conditionFunction;

    ConditionType(ConditionFunction<String, Object, AbstractConditionBuilder<?>, String> conditionFunction) {
        this.conditionFunction = conditionFunction;
    }

    public static String resolve(ConditionType conditionType, String column, Object param, AbstractConditionBuilder<?> builder) {
        return conditionType.conditionFunction.apply(column, param, builder);
    }

    private static String assemIn(Collection<?> param, AbstractConditionBuilder<?> builder) {
        return param.stream()
                .map(arg -> getWrapSegment(builder, arg))
                .collect(Collectors.joining(SqlConstant.COMMA_, SqlConstant.PRE_BRACKET, SqlConstant.POST_BRACKET));
    }

}
