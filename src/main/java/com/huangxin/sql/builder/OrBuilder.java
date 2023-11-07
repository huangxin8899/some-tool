package com.huangxin.sql.builder;

import java.util.Map;
import java.util.function.Consumer;

/**
 * OrBuilder
 *
 * @author huangxin
 */
public class OrBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<OrBuilder<T>> {

    public OrBuilder(Map<String, Object> paramMap, Map<Class<?>, String> aliasMap ,Consumer<OrBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.aliasMap = aliasMap;
        this.consumer = consumer;
    }
}
