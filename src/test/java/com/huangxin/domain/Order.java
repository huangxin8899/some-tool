package com.huangxin.domain;

import com.huangxin.sql.anno.Condition;
import com.huangxin.sql.type.ConditionType;
import lombok.Data;

/**
 * Order
 *
 * @author 黄鑫
 */
@Data
public class Order {

    @Condition
    private Integer id;

    @Condition(type = ConditionType.LIKE)
    private String name;

    @Condition(fieldName = "aged")
    private Integer age;

    private Integer deleteFlag;
}
