package com.huangxin.sql.builder;

import java.util.Map;
import java.util.function.Consumer;

/**
 * HavingBuilder
 *
 * @author huangxin
 */
public class HavingBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<HavingBuilder<T>> {

    public HavingBuilder(Map<String, Object> paramMap, Map<Class<?>, String> aliasMap, Consumer<HavingBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.aliasMap = aliasMap;
        this.consumer = consumer;
    }
}
