package com.huangxin.mybatis.builder;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.constant.SqlConstant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * AndBuilder
 *
 * @author huangxin
 */
public class AndBuilder<T extends ConditionBuilder<T>> extends CommonConditionBuilder<AndBuilder<T>>  {

    public AndBuilder(Map<String, Object> paramMap, Consumer<AndBuilder<T>> consumer) {
        this.paramMap = paramMap;
        this.consumer = consumer;
    }
}
