package com.huangxin.mybatis.builder;

import lombok.Getter;
import org.apache.ibatis.jdbc.SQL;

import java.util.HashMap;
import java.util.Map;

/**
 * SqlEntity
 *
 * @author 黄鑫
 */
public class SqlEntity {

    protected String table;
    protected Class<?> resultClass;
    protected final SQL sql = new SQL();

    @Getter
    protected final Map<String, Object> paramMap = new HashMap<>();
}
