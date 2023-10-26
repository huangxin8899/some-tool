package com.huangxin.mybatis.builder;

/**
 * SqlBuilder
 *
 * @author 黄鑫
 */
public class SqlBuilder {

    public <Query> SelectBuilder query(Query queryParam) {

        return new SelectBuilder();
    }
}
