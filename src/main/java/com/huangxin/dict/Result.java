package com.huangxin.dict;

import lombok.Data;

/**
 * Result
 *
 * @author 黄鑫
 */
@Data
public class Result<T> {

    private int code;
    private String msg;
    private T data;
}
