package com.huangxin.domain;

import com.huangxin.sql.anno.SelectColumn;

/**
 * UserVO
 *
 * @author 黄鑫
 */
public class UserVO {

    private Integer id;
    private String name;
    private Integer age;
    @SelectColumn(table = Order.class)
    private Integer deleteFlag;
    @SelectColumn("user.version")
    private String version;
}
