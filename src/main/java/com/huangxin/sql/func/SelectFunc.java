package com.huangxin.sql.func;

import java.io.Serializable;

/**
 * SelectFunc
 *
 * @author 黄鑫
 */
@FunctionalInterface
public interface SelectFunc extends Serializable {

    void exec();
}
