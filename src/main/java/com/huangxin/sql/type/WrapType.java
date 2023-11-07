package com.huangxin.sql.type;

import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.constant.SqlConstant;
import com.huangxin.sql.func.SerializableFunction;
import com.huangxin.sql.util.BuilderUtil;
import com.huangxin.sql.util.FunctionUtil;

import java.util.Map;
import java.util.function.BiFunction;

public enum WrapType {
    // 默认 ARG0、AGR1……自增
    AUTO((paramMap, param) -> {
        String nextKey = SqlConstant.ARG + paramMap.size();
        paramMap.put(nextKey, param);
        return SqlConstant.wrapParam(nextKey);
    }),

    // 直接toString()输出
    UNALTERED((paramMap, param) -> StrUtil.format("'{}'", param.toString())),

    // 自定义占位符 paramMap会以{"0":param,"1":param,……}记录参数
    PLACEHOLDER((paramMap, param) -> {
        paramMap.put(String.valueOf(paramMap.size()), param);
        return BuilderConfig.PLACEHOLDER;
    }),

    // 自定义
    CUSTOM((paramMap, param) -> BuilderConfig.PLACEHOLDER),
    ;

    private final BiFunction<Map<String, Object>, Object, String> strategy;

    WrapType(BiFunction<Map<String, Object>, Object, String> strategy) {
        this.strategy = strategy;
    }

    public static String getWrapSegment(Map<String, Object> paramMap, Object param) {
        if (param instanceof SerializableFunction) {
            return FunctionUtil.getMetaColumn((SerializableFunction) param).wrapTableDotColumn(BuilderUtil.get().getAliasMap());
        }
        return BuilderConfig.WRAP_TYPE.strategy.apply(paramMap, param);
    }
}
