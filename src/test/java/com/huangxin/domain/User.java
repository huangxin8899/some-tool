package com.huangxin.domain;

import com.huangxin.excel.annotation.ExtConfig;
import com.huangxin.mybatis.ConditionType;
import com.huangxin.mybatis.anno.Column;
import com.huangxin.mybatis.anno.ConditionFlag;
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

    @ConditionFlag
    private Integer id;

    @ExtConfig(autoMerge = true, replace = {"bb_aa"})
    @ConditionFlag(type = ConditionType.LIKE)
    private String name;

    @ConditionFlag(fieldName = "aged")
    private Integer age;

    public static User getOne() {
        return new User(1, "user", 12);
    }
}
