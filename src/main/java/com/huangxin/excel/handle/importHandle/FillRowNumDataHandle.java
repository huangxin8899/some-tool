package com.huangxin.excel.handle.importHandle;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * fillRowNumDataHandle
 *
 * @author 黄鑫
 */
@Slf4j
public class FillRowNumDataHandle implements DataHandle {

    private String fieldName = "rowNum";
    public FillRowNumDataHandle() {
    }

    public FillRowNumDataHandle(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public <T> boolean rowProcess(T t, int rowNum, List<String> msgList) {
        Class<?> clazz = t.getClass();
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, rowNum);
        } catch (IllegalAccessException e) {
            log.error("==> 给行号字段赋值异常", e);
            throw new RuntimeException("给行号字段赋值异常");
        } catch (NoSuchFieldException e) {
            log.error("==> 获取行号字段异常", e);
            throw new RuntimeException("获取行号字段异常");
        }
        return true;
    }
}
