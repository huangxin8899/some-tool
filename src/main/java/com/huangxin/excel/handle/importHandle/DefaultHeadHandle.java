package com.huangxin.excel.handle.importHandle;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * DefaultHeadHandle
 *
 * @author 黄鑫
 */
public class DefaultHeadHandle implements HeadHandle {
    private static final String UID = "serialVersionUID";

    @Override
    public void headProcess(Field[] fields, List<String> head, Integer headNum, Map<Integer, String> headMap, AnalysisContext context) {
        if (ObjectUtil.isEmpty(head)) {
            //获取泛型类型
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (UID.equals(field.getName())) {
                    continue;
                }
                ExcelProperty fieldAnnotation = field.getAnnotation(ExcelProperty.class);
                if (fieldAnnotation != null) {
                    int index = fieldAnnotation.index() != -1 ? fieldAnnotation.index() : i;
                    String headName = headMap.get(index);
                    String verifyName = "";
                    for (int j = 0; j < headNum; j++) {
                        verifyName = fieldAnnotation.value()[j];
                    }
                    if (StrUtil.isEmpty(headName) || !headName.equals(verifyName)) {
                        throw new RuntimeException("模板错误，请检查导入模板");
                    }
                }
            }
        } else {
            for (int i = 0; i < head.size(); i++) {
                String headName = headMap.get(i);
                String currentName = head.get(i);
                if (headName == null) {
                    throw new RuntimeException("模板错误，请检查导入模板");
                }
                boolean equals = (i != 0) && currentName.equals(head.get(i - 1));
                String verifyName = equals ? null : currentName;
                if (!headName.equals(verifyName)) {
                    throw new RuntimeException("模板错误，请检查导入模板");
                }
            }
        }
    }
}
