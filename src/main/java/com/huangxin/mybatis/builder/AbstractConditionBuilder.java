package com.huangxin.mybatis.builder;

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

    protected final List<String> whereList = new ArrayList<>();
    protected final List<String> andList = new ArrayList<>();
    protected final Map<String, List<String>> andMap = createAndMap();
    protected final List<List<String>> orList = new ArrayList<>();
    protected Boolean isOr = Boolean.FALSE;
    protected Boolean isAnd = Boolean.FALSE;

    private Map<String, List<String>> createAndMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("or", new ArrayList<>());
        map.put("and", new ArrayList<>());
        return map;
    }
}
