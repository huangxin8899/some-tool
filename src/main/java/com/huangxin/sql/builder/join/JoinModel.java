package com.huangxin.sql.builder.join;

/**
 * JoinModel
 *
 * @author 黄鑫
 */
public interface JoinModel<T> extends LeftJoinModel<T>, RightJoinModel<T>, InnerJoinModel<T>, OuterJoinModel<T> {
}
