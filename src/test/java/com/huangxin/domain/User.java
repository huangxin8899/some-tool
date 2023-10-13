package com.huangxin.domain;

import com.huangxin.excel.annotation.ExtConfig;
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
    private Integer id;
    @ExtConfig(autoMerge = true, replace = {"bb_aa"})
    private String name;
    private Integer age;
}
