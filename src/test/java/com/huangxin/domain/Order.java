package com.huangxin.domain;

import com.huangxin.sql.anno.ConditionFlag;
import com.huangxin.sql.type.ConditionType;
import lombok.Data;

/**
 * Order
 *
 * @author 黄鑫
 */
@Data
public class Order {

    @ConditionFlag
    private Integer id;

    @ConditionFlag(type = ConditionType.LIKE)
    private String name;

    @ConditionFlag(fieldName = "aged")
    private Integer age;

    private Integer deleteFlag;
}
