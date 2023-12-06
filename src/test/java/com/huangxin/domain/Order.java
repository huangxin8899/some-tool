package com.huangxin.domain;

import com.huangxin.sql.anno.Column;
import com.huangxin.sql.anno.Condition;
import com.huangxin.sql.anno.Table;
import com.huangxin.sql.type.ConditionType;
import lombok.Data;

/**
 * Order
 *
 * @author 黄鑫
 */
@Data
@Table("`order`")
public class Order {

    @Condition
    @Column("`id`")
    private Integer id;

    @Condition(type = ConditionType.LIKE)
    @Column("`name`")
    private String name;

    @Condition(fieldName = "aged")
    @Column("`age`")
    private Integer age;

    private Integer deleteFlag;
}
