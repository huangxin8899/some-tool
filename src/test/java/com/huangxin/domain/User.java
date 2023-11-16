package com.huangxin.domain;

import com.huangxin.excel.annotation.ExtConfig;
import com.huangxin.sql.type.ConditionType;
import com.huangxin.sql.anno.Condition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User
 *
 * @author 黄鑫
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Condition
    private Integer id;

    @ExtConfig(autoMerge = true, replace = {"bb_aa"})
    @Condition(type = ConditionType.LIKE)
    private String name;

    @Condition(fieldName = "aged")
    private Integer age;

    public static User getOne() {
        return new User(1, "user", 12);
    }
}
