package com.huangxin.sql.entity;

import org.apache.ibatis.jdbc.AbstractSQL;

import java.io.Serializable;

/**
 * Sql
 *
 * @author 黄鑫
 */
public class Sql extends AbstractSQL<Sql> implements Serializable {
    @Override
    public Sql getSelf() {
        return this;
    }
}
