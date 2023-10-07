package com.huangxin.excel.handle.importHandle;

import com.alibaba.excel.context.AnalysisContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * HeadHandle
 *
 * @author 黄鑫
 */
public interface HeadHandle {

    /**
     * 可自定义校验表头
     * @param fields    表头对应的字段
     * @param head      表头字符串集合
     * @param headNum   表头行数（不包含非表头的提示行）
     * @param headMap   表头的Map
     * @param context   easyexcel的处理上下文
     */
    void headProcess(Field[] fields, List<String> head, Integer headNum, Map<Integer, String> headMap, AnalysisContext context);
}
