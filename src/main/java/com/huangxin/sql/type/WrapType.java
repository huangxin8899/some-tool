package com.huangxin.sql.type;

import cn.hutool.core.date.DateUtil;
import com.huangxin.sql.config.BuilderConfig;
import com.huangxin.sql.entity.BaseBuilder;
import com.huangxin.sql.expression.MybatisExpression;
import com.huangxin.sql.expression.StringExpression;
import com.huangxin.sql.func.SerializableFunction;
import net.sf.jsqlparser.expression.*;

import java.util.Date;
import java.util.function.BiFunction;

public enum WrapType {

    AUTO((builder, param) -> {
        if (param instanceof String) {
            return new StringValue(param.toString());
        } else if (param instanceof Integer || param instanceof Long) {
            return new LongValue(param.toString());
        } else if (param instanceof Double) {
            return new DoubleValue(param.toString());
        } else if (param instanceof Date) {
            return new StringValue(DateUtil.formatDateTime((Date) param));
        } else {
            return new StringExpression(param);
        }
    }),

    // 直接toString()输出
    STRING((builder, param) -> new StringExpression(param)),

    // 默认 #{param0}、#{param1}……自增
    MYBATIS((builder, param) -> new MybatisExpression(builder.nextParamName(param))),

    // ":param0 :param0"占位
    JDBC((builder, param) -> new JdbcNamedParameter(builder.nextParamName(param))),
    ;

    private final BiFunction<BaseBuilder, Object, Expression> strategy;

    WrapType(BiFunction<BaseBuilder, Object, Expression> strategy) {
        this.strategy = strategy;
    }

    public static Expression getWrapExpression(BaseBuilder builder, Object param) {
        if (param instanceof SerializableFunction) {
            return builder.getColumn((SerializableFunction) param);
        }
        return BuilderConfig.WRAP_TYPE.strategy.apply(builder, param);
    }
}
