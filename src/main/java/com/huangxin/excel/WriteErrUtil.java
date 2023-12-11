package com.huangxin.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.huangxin.excel.handle.importHandle.ErrMsgWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * WriteErrUtil
 *
 * @author 黄鑫
 */
public class WriteErrUtil {

    public static void writeErrMsgAndUpload(File sourceFile, int titleNum, Map<Integer, List<String>> errorMap) {
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

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                Optional.ofNullable(errMsgFile)
                        .ifPresent(File::deleteOnExit);
            }
        }
    }
}
