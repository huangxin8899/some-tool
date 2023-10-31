package com.huangxin.domain;

import com.huangxin.excel.annotation.ExtConfig;
import com.huangxin.mybatis.anno.ConditionFlag;
import com.huangxin.mybatis.type.ConditionType;
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
