package com.huangxin.excel.handle.importHandle;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 验证器接口
 *
 * @author 黄鑫
 */
public interface DataHandle {

    /**
     * 校验行数据
     * @param t 行数据
     * @param msgList 行号对应的错误消息
     */
    default <T> boolean rowProcess(T t, int rowNum, List<String> msgList, Class<?>... groups) {
        return true;
    }


    /**
     * 校验字段数据
     *
     * @param field      字段对象
     * @param fieldValue 字段值
     * @param msgList    行号对应的错误消息
     */
    default <T> void fieldProcess(T t, Field field, Object fieldValue, List<String> msgList) {
    }
}
