package com.huangxin.domain;

import com.huangxin.excel.annotation.ExtConfig;
import com.huangxin.sql.anno.Column;
import com.huangxin.sql.anno.Table;
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
@Table("`user`")
public class User {

    @Condition
    @Column("`id`")
    private Integer id;

    @ExtConfig(autoMerge = true, replace = {"bb_aa"})
    @Condition(type = ConditionType.LIKE)
    @Column("`name`")
    private String name;

    @Condition(fieldName = "aged")
    @Column("`age`")
    private Integer age;

    public static User getOne() {
        return new User(1, "user", 12);
    }
}
