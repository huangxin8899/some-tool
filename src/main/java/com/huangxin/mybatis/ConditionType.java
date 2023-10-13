package com.huangxin.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.*;

public enum ConditionType {
    //相等.
    EQ,
    //不等于 .
    NE,
    //大于.
    GT,
    //大于等于.
    GE,
    //小于.
    LT,
    //小于等于.
    LE,
    //中间模糊 %Value%.
    LIKE,
    //not like %Value%.
    NOT_LIKE,
    //左模糊 %Value.
    LEFT_LIKE,
    //右模糊 Value%.
    RIGHT_LIKE,
    //IN.(String类型用","隔开，集合调用的是toString方法)
    IN,
    //NOT IN.(String类型用","隔开，集合调用的是toString方法)
    NOT_IN,
    //在两个值之间.(String类型用","隔开，集合只取前两个)
    BETWEEN,
    //不在两个值之间.(String类型用","隔开，集合只取前两个)
    NOT_BETWEEN,
    //为空.
    IS_NULL,
    //不为空.
    IS_NOT_NULL;

    public static String resolve(ConditionType conditionType, String column, Object param, Map<String, Object> paramMap) {
        StringBuilder segment = new StringBuilder();
        switch (conditionType) {
            case IS_NULL:
                segment.append(column)
                        .append(SqlConstant._IS_)
                        .append(SqlConstant.NULL);
                return segment.toString();
            case IS_NOT_NULL:
                segment.append(column)
                        .append(SqlConstant._IS_)
                        .append(SqlConstant.NOT_)
                        .append(SqlConstant.NULL);
                return segment.toString();
            default:
                break;
        }
        if (ObjectUtil.isEmpty(param)) {
            return null;
        }
        String paramName = SqlConstant.ARG + paramMap.size();
        switch (conditionType) {
            case EQ:
                segment.append(column)
                        .append(SqlConstant._EQUAL_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case NE:
                segment.append(column)
                        .append(SqlConstant._NOT_EQUAL_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case GT:
                segment.append(column)
                        .append(SqlConstant._GT_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case GE:
                segment.append(column)
                        .append(SqlConstant._GE_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case LT:
                segment.append(column)
                        .append(SqlConstant._LT_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case LE:
                segment.append(column)
                        .append(SqlConstant._LE_)
                        .append(SqlConstant.wrapParam(paramName));
                paramMap.put(paramName, param);
                break;
            case LIKE:
                segment.append(column)
                        .append(SqlConstant._LIKE_)
                        .append("CONCAT('%',")
                        .append(SqlConstant.wrapParam(paramName))
                        .append(",'%')");
                paramMap.put(paramName, param);
                break;
            case NOT_LIKE:
                segment.append(column)
                        .append(SqlConstant._NOT_)
                        .append(SqlConstant.LIKE_)
                        .append("CONCAT('%',")
                        .append(SqlConstant.wrapParam(paramName))
                        .append(",'%')");
                paramMap.put(paramName, param);
                break;
            case LEFT_LIKE:
                segment.append(column)
                        .append(SqlConstant._LIKE_)
                        .append("CONCAT('%',")
                        .append(SqlConstant.wrapParam(paramName))
                        .append(")");
                paramMap.put(paramName, param);
                break;
            case RIGHT_LIKE:
                segment.append(column)
                        .append(SqlConstant._LIKE_)
                        .append("CONCAT(")
                        .append(SqlConstant.wrapParam(paramName))
                        .append(",'%')");
                paramMap.put(paramName, param);
                break;
            case IN:
                if (param instanceof String) {
                    List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
                    segment.append(column)
                            .append(SqlConstant.IN_)
                            .append(SqlConstant.PRE_BRACKET)
                            .append(assemIn(list, paramMap))
                            .append(SqlConstant.POST_BRACKET);
                } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
                    segment.append(column)
                            .append(SqlConstant.IN_)
                            .append(SqlConstant.PRE_BRACKET)
                            .append(assemIn((Collection<?>) param, paramMap))
                            .append(SqlConstant.POST_BRACKET);
                }
                break;
            case NOT_IN:
                if (param instanceof String) {
                    List<String> list = Arrays.asList(param.toString().split(SqlConstant.COMMA));
                    segment.append(column)
                            .append(SqlConstant._NOT_)
                            .append(SqlConstant.IN_)
                            .append(SqlConstant.PRE_BRACKET)
                            .append(assemIn(list, paramMap))
                            .append(SqlConstant.POST_BRACKET);
                } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
                    segment.append(column)
                            .append(SqlConstant._NOT_)
                            .append(SqlConstant.IN_)
                            .append(SqlConstant.PRE_BRACKET)
                            .append(assemIn((Collection<?>) param, paramMap))
                            .append(SqlConstant.POST_BRACKET);
                }
                break;
            case BETWEEN:
                if (param instanceof String) {
                    String[] arg = param.toString().split(SqlConstant.COMMA);
                    String argName1 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName1, arg[0]);
                    String argName2 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName2, arg[1]);
                    segment.append(column)
                            .append(SqlConstant._BETWEEN_)
                            .append(argName1)
                            .append(SqlConstant._AND_)
                            .append(argName2);
                } else if (param instanceof List<?> && CollUtil.isNotEmpty((List<?>) param)) {
                    Iterator<?> iterator = ((Collection<?>) param).iterator();
                    Object next1 = iterator.next();
                    Object next2 = iterator.next();
                    String argName1 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName1, next1);
                    String argName2 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName2, next2);
                    segment.append(column)
                            .append(SqlConstant._BETWEEN_)
                            .append(argName1)
                            .append(SqlConstant._AND_)
                            .append(argName2);
                }
                break;
            case NOT_BETWEEN:
                if (param instanceof String) {
                    String[] arg = param.toString().split(SqlConstant.COMMA);
                    String argName1 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName1, arg[0]);
                    String argName2 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName2, arg[1]);
                    segment.append(column)
                            .append(SqlConstant._NOT_)
                            .append(SqlConstant.BETWEEN_)
                            .append(argName1)
                            .append(SqlConstant._AND_)
                            .append(argName2);
                } else if (param instanceof Collection<?> && CollUtil.isNotEmpty((Collection<?>) param)) {
                    Iterator<?> iterator = ((Collection<?>) param).iterator();
                    Object next1 = iterator.next();
                    Object next2 = iterator.next();
                    String argName1 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName1, next1);
                    String argName2 = SqlConstant.ARG + paramMap.size();
                    paramMap.put(argName2, next2);
                    segment.append(column)
                            .append(SqlConstant._NOT_)
                            .append(SqlConstant.BETWEEN_)
                            .append(argName1)
                            .append(SqlConstant._AND_)
                            .append(argName2);
                }
                break;
            default:
                return null;
        }
        return segment.toString();
    }

    private static StringJoiner assemIn(Collection<?> param, Map<String, Object> paramMap) {
        StringJoiner joiner = new StringJoiner(SqlConstant.COMMA_);
        param.forEach(arg -> {
            StringBuilder argBuilder = new StringBuilder();
            String argName = SqlConstant.ARG + paramMap.size();
            argBuilder.append(SqlConstant.wrapParam(argName));
            paramMap.put(argName, arg);
            joiner.add(argBuilder);
        });
        return joiner;
    }
}
