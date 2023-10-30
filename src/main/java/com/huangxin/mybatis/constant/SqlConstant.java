package com.huangxin.mybatis.constant;

/**
 * SqlConstant
 *
 * @author 黄鑫
 */
public interface SqlConstant {
    String SELECT = "SELECT";
    String SELECT_ = "SELECT ";

    String INSERT_INTO = "INSERT INTO";
    String INSERT_INTO_ = "INSERT INTO ";

    String VALUES_ = "VALUE ";

    String ITEM = "item";

    String PRE_SCRIPT = "<script>\n";
    String POST_SCRIPT = "\n</script>";

    String PRE_FOREACH_SCRIPT = "\n<foreach collection=\"list\" separator=\",\" item=\"" + ITEM + "\">\n";
    String POST_FOREACH_SCRIPT = "\n</foreach>\n";

    String UPDATE = "UPDATE";
    String UPDATE_ = "UPDATE ";

    String POST_TRIM = "\n</trim>\n";

    String SET_SCRIPT = "\n<trim prefix=\"set\" suffixOverrides=\",\">\n";

    String PRE_CASE_SCRIPT = "\n<trim prefix=\"";
    String POST_CASE_SCRIPT = " = CASE\" suffix=\"END,\">\n";

    String WHEN = "WHEN";
    String WHEN_ = "WHEN ";


    String THEN = "THEN";
    String THEN_ = "THEN ";
    String _THEN_ = " THEN ";


    String FROM = "FROM";
    String FROM_ = "FROM ";

    String JOIN = "JOIN";
    String JOIN_ = "JOIN ";

    String LEFT_JOIN = "LEFT JOIN";
    String LEFT_JOIN_ = "LEFT JOIN ";


    String ON = "ON";
    String ON_ = "ON ";
    String _ON_ = " ON ";

    String WHERE = "WHERE";
    String WHERE_ = "WHERE ";
    String _WHERE_ = " WHERE ";


    String AND = "AND";
    String AND_ = "AND ";
    String _AND_ = " AND ";

    String AS = "AS";
    String _AS = " AS";
    String AS_ = "AS ";
    String _AS_ = " AS ";

    String BETWEEN = "BETWEEN";
    String BETWEEN_ = "BETWEEN ";
    String _BETWEEN_ = " BETWEEN ";

    String DOT = ".";

    String SPACE = " ";

    String COMMA = ",";
    String COMMA_ = ", ";

    String EQUAL = "=";
    String EQUAL_ = "= ";
    String _EQUAL_ = " = ";

    String NOT_EQUAL = "<>";
    String NOT_EQUAL_ = "<> ";
    String _NOT_EQUAL_ = " <> ";

    String GT = ">";
    String GT_ = "> ";
    String _GT_ = " > ";

    String GE = ">=";
    String GE_ = ">= ";
    String _GE_ = " >= ";

    String LT = "<";
    String LT_ = "< ";
    String _LT_ = " < ";

    String LE = "<=";
    String LE_ = "<= ";
    String _LE_ = " <= ";

    String _LIKE_ = " LIKE ";
    String LIKE_ = "LIKE ";

    String ARG = "arg";

    String PRE_PARAM = "#{";
    String POST_PARAM = "}";

    String PRE_BRACKET = "(";
    String _PRE_BRACKET = " (";
    String POST_BRACKET = ")";
    String POST_BRACKET_ = ")";

    String PRE_CONCAT = "CONCAT('%',";
    String POST_CONCAT = ",'%')";

    String NOT = "NOT";
    String NOT_ = "NOT ";
    String _NOT_ = " NOT ";

    String IN = "IN";
    String IN_ = "IN ";
    String _IN_ = " IN ";

    String IS = "IS";
    String IS_ = "IS ";
    String _IS_ = " IS ";

    String NULL = "NULL";
    String NULL_ = " NULL ";
    String _NULL_ = " NULL ";

    String ASC = "ASC";
    String _ASC = " ASC";
    String ASC_ = "ASC ";
    String _ASC_ = " ASC ";

    String DESC = "DESC";
    String _DESC = " DESC";
    String DESC_ = "DESC ";
    String _DESC_ = " DESC ";

    String BACK_QUOTE = "`";

    static String wrapBackQuote(String string) {
        return BACK_QUOTE + string + BACK_QUOTE;
    }

    static String wrapParam(String paramName) {
        return PRE_PARAM + paramName + POST_PARAM;
    }
}
