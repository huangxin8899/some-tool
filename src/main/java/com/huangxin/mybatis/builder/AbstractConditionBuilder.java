package com.huangxin.mybatis.builder;

import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AbstractConditionBuilder
 *
 * @author 黄鑫
 */
public abstract class AbstractConditionBuilder<T extends AbstractConditionBuilder<T>>
        extends SqlEntity
        implements ConditionBuilder<T> {

    protected final SQL sql = new SQL();
    protected final List<String> whereList = new ArrayList<>();
    protected final List<List<String>> orList = new ArrayList<>();
    protected Boolean isOr = Boolean.FALSE;
}
