package com.huangxin.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.huangxin.excel.handle.importHandle.ErrMsgWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WriteErrUtil
 *
 * @author 黄鑫
 */
public class WriteErrUtil {

    /**
     * 给导入excel追加错误信息.
     *
     * @param sourceFile 导入的excel文件.
     * @param titleNum   表头行数索引.
     * @param errColl    错误信息集合.
     * @param row        行号函数.
     * @param errMsg     错误信息函数.
     * @param <T>        错误信息对象.
     * @return 上传文件的URL.
     */
    public static <T> String writeErrMsgAndUpload(File sourceFile, int titleNum, Collection<T> errColl, Function<T, Integer> row, Function<T, ?> errMsg) {
        if (CollUtil.isNotEmpty(errColl)) {
            Map<Integer, ?> errMap = errColl.stream().collect(Collectors.toMap(row, errMsg));
            return writeErrMsgAndUpload(sourceFile, titleNum, errMap);
        }
        return null;
    }

    /**
     * 给导入excel追加错误信息.
     *
     * @param sourceFile 导入的excel文件.
     * @param titleNum   表头行数索引.
     * @param errorMap   错误信息(key-行数，value-错误信息的字符串或字符串集合（string or List<String>）).
     * @return 上传文件的URL.
     */
    public static String writeErrMsgAndUpload(File sourceFile, int titleNum, Map<Integer, ?> errorMap) {
        if (!errorMap.isEmpty()) {
            File errMsgFile = null;
            try {
                errMsgFile = File.createTempFile(IdUtil.fastSimpleUUID(), FileUtil.getSuffix(sourceFile));
                // 追加写入
                EasyExcel.write(errMsgFile)
                        .withTemplate(sourceFile)
                        .sheet()
                        .registerWriteHandler(new ErrMsgWriter(errorMap, titleNum))
                        .doWrite(new ArrayList<>());
                // 上传
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                Optional.ofNullable(errMsgFile)
                        .ifPresent(File::deleteOnExit);
            }
        }
        return null;
    }
}
