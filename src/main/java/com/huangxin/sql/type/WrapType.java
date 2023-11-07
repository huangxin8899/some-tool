package com.huangxin.sql.type;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.builder.AbstractConditionBuilder;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.util.FunctionUtil;

import java.util.Map;
import java.util.function.BiFunction;

public enum WrapType {
    // 默认 ARG0、AGR1……自增
    AUTO((builder, param) -> {
        Map<String, Object> paramMap = builder.getParamMap();
        String nextKey = SqlConstant.ARG + paramMap.size();
        paramMap.put(nextKey, param);
        return SqlConstant.wrapParam(nextKey);
    }),

    // 直接toString()输出
    UNALTERED((builder, param) -> StrUtil.format("'{}'", param.toString())),

    // 自定义占位符 paramMap会以{"0":param,"1":param,……}记录参数
    PLACEHOLDER((builder, param) -> {
        Map<String, Object> paramMap = builder.getParamMap();
        paramMap.put(String.valueOf(paramMap.size()), param);
        return BuilderConfig.PLACEHOLDER;
    }),

    // 自定义
    CUSTOM((builder, param) -> BuilderConfig.PLACEHOLDER),
    ;

    private final BiFunction<AbstractConditionBuilder<?>, Object, String> strategy;

    WrapType(BiFunction<AbstractConditionBuilder<?>, Object, String> strategy) {
        this.strategy = strategy;
    }

    public static String getWrapSegment(AbstractConditionBuilder<?> builder, Object param) {
        if (param instanceof SerializableFunction) {
            return FunctionUtil.getMetaColumn((SerializableFunction) param).wrapTableDotColumn(builder.getAliasMap());
        }
        return BuilderConfig.WRAP_TYPE.strategy.apply(builder, param);
    }
}
