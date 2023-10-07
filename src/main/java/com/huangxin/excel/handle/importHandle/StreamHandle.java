package com.huangxin.excel.handle.importHandle;

/**
 * StreamHandle
 *
 * @author 黄鑫
 */
@FunctionalInterface
public interface StreamHandle<T> {

    boolean exec(T t);

}
