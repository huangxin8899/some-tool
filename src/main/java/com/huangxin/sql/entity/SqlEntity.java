package com.huangxin.sql.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * SqlEntity
 *
 * @author 黄鑫
 */
public class SqlEntity implements Serializable {

    protected String table;
    protected Class<?> resultClass;
    protected final Sql sql = new Sql();

    @Getter
    protected Map<String, Object> paramMap = new HashMap<>();


}
