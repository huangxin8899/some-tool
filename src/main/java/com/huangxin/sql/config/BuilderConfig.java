package com.huangxin.sql.config;

import com.huangxin.sql.type.WrapType;

/**
 * BuilderConfig
 *
 * @author 黄鑫
 */
public interface BuilderConfig {


    /**
     * 条件参数的策略
     */
    WrapType WRAP_TYPE = WrapType.AUTO;

    /**
     * 更新是否允许NULL
     */
    boolean ALLOW_NULL = false;
}
