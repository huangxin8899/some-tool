package com.huangxin.sql.builder;

import java.util.Map;
import java.util.function.Consumer;

/**
 * AndBuilder
 *
 * @author huangxin
 */
public class AndBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<AndBuilder<T>>  {

    public AndBuilder(Map<String, Object> paramMap, Map<Class<?>, String> aliasMap, Consumer<AndBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.aliasMap = aliasMap;
        this.consumer = consumer;
    }
}
