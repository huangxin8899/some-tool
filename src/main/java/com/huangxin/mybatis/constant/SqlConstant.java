package com.huangxin.mybatis.constant;

import cn.hutool.core.util.StrUtil;

/**
 * SqlConstant
 *
 * @author 黄鑫
 */
public interface SqlConstant {

    String _ON_ = " ON ";

    String _AND_ = " AND ";

    String COMMA = ",";
    String COMMA_ = ", ";

    String ARG = "arg";

    String PRE_BRACKET = "(";
    String POST_BRACKET = ")";


    String _ASC = " ASC";

    String _DESC = " DESC";

    static String wrapBackQuote(String string) {
        return StrUtil.format("`{}`", string);
    }

    static String wrapParam(String paramName) {
        return StrUtil.format("#{{}}", paramName);
    }
}
