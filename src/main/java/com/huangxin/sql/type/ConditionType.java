package com.huangxin.sql.type;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.huangxin.sql.builder.AbstractConditionBuilder;
import com.huangxin.sql.func.ConditionFunction;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.huangxin.sql.type.WrapType.getWrapExpression;

public enum ConditionType {
    //相等.
    EQ((column, param, builder) -> new EqualsTo(column, getWrapExpression(builder, param))),

    //不等于.
    NE((column, param, builder) -> new NotEqualsTo(column, getWrapExpression(builder, param))),

    //大于.
    GT((column, param, builder) -> new GreaterThan().withLeftExpression(column).withRightExpression(getWrapExpression(builder, param))),

    //大于等于.
    GE((column, param, builder) -> new GreaterThanEquals().withLeftExpression(column).withRightExpression(getWrapExpression(builder, param))),

    //小于.
    LT((column, param, builder) -> new MinorThan().withLeftExpression(column).withRightExpression(getWrapExpression(builder, param))),

    //小于等于.
    LE((column, param, builder) -> new MinorThanEquals().withLeftExpression(column).withRightExpression(getWrapExpression(builder, param))),

    //中间模糊 %Value%.
    LIKE((column, param, builder) -> {
        LikeExpression likeExpression = new LikeExpression();
        ExpressionList parameters = new ExpressionList(Arrays.asList(new StringValue("%"), getWrapExpression(builder, param), new StringValue("%")));
        Function function = new Function().withName("CONCAT").withParameters(parameters);
        return likeExpression.withLeftExpression(column).withRightExpression(function);
    }),

    //not like %Value%.
    NOT_LIKE((column, param, builder) -> {
        LikeExpression likeExpression = new LikeExpression();
        ExpressionList parameters = new ExpressionList(Arrays.asList(new StringValue("%"), getWrapExpression(builder, param), new StringValue("%")));
        Function function = new Function().withName("CONCAT").withParameters(parameters);
        return likeExpression.withNot(true).withLeftExpression(column).withRightExpression(function);
    }),

    //左模糊 %Value.
    LEFT_LIKE((column, param, builder) -> {
        LikeExpression likeExpression = new LikeExpression();
        ExpressionList parameters = new ExpressionList(Arrays.asList(new StringValue("%"), getWrapExpression(builder, param)));
        Function function = new Function().withName("CONCAT").withParameters(parameters);
        return likeExpression.withLeftExpression(column).withRightExpression(function);
    }),

    //右模糊 Value%.
    RIGHT_LIKE((column, param, builder) -> {
        LikeExpression likeExpression = new LikeExpression();
        ExpressionList parameters = new ExpressionList(Arrays.asList(getWrapExpression(builder, param), new StringValue("%")));
        Function function = new Function().withName("CONCAT").withParameters(parameters);
        return likeExpression.withLeftExpression(column).withRightExpression(function);
    }),

    //IN (String类型用","隔开，集合调用的是toString方法).
    IN((column, param, builder) -> {
        if (param instanceof String) {
            List<Expression> parameters = Arrays.stream(param.toString().split(StrUtil.COMMA)).map(s -> getWrapExpression(builder, s)).collect(Collectors.toList());
            ExpressionList itemsList = new ExpressionList();
            itemsList.addExpressions(parameters);
            return new InExpression(column, itemsList);
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            List<Expression> parameters = ((Collection<?>) param).stream().map(s -> getWrapExpression(builder, s)).collect(Collectors.toList());
            ExpressionList itemsList = new ExpressionList();
            itemsList.addExpressions(parameters);
            return new InExpression(column, itemsList);
        }
        return null;
    }),

    //NOT IN.(String类型用","隔开，集合调用的是toString方法)
    NOT_IN((column, param, builder) -> {
        if (param instanceof String) {
            List<Expression> parameters = Arrays.stream(param.toString().split(StrUtil.COMMA)).map(s -> getWrapExpression(builder, s)).collect(Collectors.toList());
            ExpressionList itemsList = new ExpressionList();
            itemsList.addExpressions(parameters);
            return new InExpression(column, itemsList).withNot(true);
        } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
            List<Expression> parameters = ((Collection<?>) param).stream().map(s -> getWrapExpression(builder, s)).collect(Collectors.toList());
            ExpressionList itemsList = new ExpressionList();
            itemsList.addExpressions(parameters);
            return new InExpression(column, itemsList).withNot(true);
        }
        return null;
    }),

    //在两个值之间.(String类型用","隔开，集合只取前两个)
    BETWEEN((column, param, builder) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(StrUtil.COMMA);
            return new Between()
                    .withLeftExpression(column)
                    .withBetweenExpressionStart(getWrapExpression(builder, arg[0]))
                    .withBetweenExpressionEnd(getWrapExpression(builder, arg[1]));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return new Between()
                    .withLeftExpression(column)
                    .withBetweenExpressionStart(getWrapExpression(builder, iterator.next()))
                    .withBetweenExpressionEnd(getWrapExpression(builder, iterator.next()));
        }
        return null;
    }),

    //不在两个值之间.(String类型用","隔开，集合只取前两个)
    NOT_BETWEEN((column, param, builder) -> {
        if (param instanceof String) {
            String[] arg = param.toString().split(StrUtil.COMMA);
            return new Between().withNot(true)
                    .withLeftExpression(column)
                    .withBetweenExpressionStart(getWrapExpression(builder, arg[0]))
                    .withBetweenExpressionEnd(getWrapExpression(builder, arg[1]));
        } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
            Iterator<?> iterator = ((Collection<?>) param).iterator();
            return new Between().withNot(true)
                    .withLeftExpression(column)
                    .withBetweenExpressionStart(getWrapExpression(builder, iterator.next()))
                    .withBetweenExpressionEnd(getWrapExpression(builder, iterator.next()));
        }
        return null;
    }),

    //为空.
    IS_NULL((column, param, builder) -> new IsNullExpression().withLeftExpression(column)),

    //不为空.
    IS_NOT_NULL((column, param, builder) -> new IsNullExpression().withNot(true).withLeftExpression(column)),
    ;

    private final ConditionFunction<Column, Object, AbstractConditionBuilder<?>, Expression> conditionFunction;

    ConditionType(ConditionFunction<Column, Object, AbstractConditionBuilder<?>, Expression> conditionFunction) {
        this.conditionFunction = conditionFunction;
    }

    public static Expression resolve(ConditionType conditionType, Column column, Object param, AbstractConditionBuilder<?> builder) {
        return conditionType.conditionFunction.apply(column, param, builder);
    }

}
