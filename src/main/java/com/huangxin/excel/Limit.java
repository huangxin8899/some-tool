package com.huangxin.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限制
 *
 * @author zhaowanjun
 * @date 2023/01/06
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Limit {
    /**
     * 文件大小限制，单位M
     */
    private Integer fileSize = 10;


    /**
     * 行数限制，去掉头部
     */
    private Integer rowNum = 10000;


    /**
     * 头部行数(包含非表头的提示行)
     */
    private Integer headNum = 1;
}
