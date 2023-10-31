package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * OrBuilder
 *
 * @author huangxin
 */
public class OrBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<OrBuilder<T>> {

    public OrBuilder(Map<String, Object> paramMap, Consumer<OrBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.consumer = consumer;
    }
}
