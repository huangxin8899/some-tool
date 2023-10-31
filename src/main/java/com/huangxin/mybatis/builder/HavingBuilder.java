package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * HavingBuilder
 *
 * @author huangxin
 */
public class HavingBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<HavingBuilder<T>> {

    public HavingBuilder(Map<String, Object> paramMap, Consumer<HavingBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.consumer = consumer;
    }
}
