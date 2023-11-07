package com.huangxin.sql.config;

import com.huangxin.sql.type.WrapType;

/**
 * BuilderConfig
 *
 * @author 黄鑫
 */
public interface BuilderConfig {

    /**
     * 更新是否允许NULL
     */
    boolean ALLOW_NULL = false;

    /**
     * 逻辑删除的字段名（不开启则为null）
     */
    String DELETE_FIELD = "deleteFlag";

    /**
     * 未逻辑删除值
     */
    String NOT_DELETE_VALUE = "0";

    /**
     * 逻辑删除值
     */
    String DELETE_VALUE = "1";

    /**
     * 默认限制条数，为负数时禁用
     */
    long DEFAULT_LIMIT = -1000;

    /**
     * 条件参数的策略
     */
    WrapType WRAP_TYPE = WrapType.PLACEHOLDER;

    String PLACEHOLDER = "?";
}
