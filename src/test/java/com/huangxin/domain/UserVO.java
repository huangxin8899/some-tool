package com.huangxin.domain;

import com.huangxin.sql.anno.SelectColumn;
import com.huangxin.sql.anno.SelectIgnore;

/**
 * UserVO
 *
 * @author 黄鑫
 */
//@SelectColumn(table = User.class)
public class UserVO {

    private Integer id;
    private String name;
//    @SelectIgnore
    private Integer age;
    @SelectColumn(table = Order.class)
    private Integer deleteFlag;
    @SelectColumn("user.version")
    private String version;
}
