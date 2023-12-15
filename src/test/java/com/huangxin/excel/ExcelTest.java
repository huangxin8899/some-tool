package com.huangxin.excel;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.huangxin.domain.User;
import com.huangxin.excel.handle.exportHandle.ReplaceWriteHandle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ExcelTest
 *
 * @author 黄鑫
 */
public class ExcelTest {

    @Test
    public void testImport() throws IOException {
        File file = new File("C:\\Users\\chenh\\Desktop\\新建 XLSX 工作表 (3).xlsx");
        File tempFile = FileUtil.file("C:\\Users\\chenh\\Desktop\\123.xlsx");
        ImportHelper<User> userImportHelper = ImportHelper.ofFile(file, User.class, new Limit(), "123");
        userImportHelper
                .notHeadNum(0)
                .page(10, users -> System.out.println("+1"));

//        EasyExcel.write(tempFile)
//                .withTemplate(file)
//                .sheet()
//                .registerWriteHandler(new ErrMsgWriter(userImportHelper.getErrorMap(), 1))
//                .doWrite(new ArrayList<>());

        WriteErrUtil.writeErrMsgAndUpload(file, 0, 1, userImportHelper.getErrorMap());


        System.out.println();
    }

    @Test
    public void testExport() {
        String fileName = "C:\\Users\\chenh\\Desktop\\新建 XLSX 工作表 (3).xlsx";
        EasyExcel.write(fileName, User.class)
                .sheet()
//                .registerWriteHandler(new DefaultMergeStrategy())
                .registerWriteHandler(new ReplaceWriteHandle())
                .doWrite(ExcelTest::getList);
    }

    private static List<User> getList() {
        return Arrays.asList(new User(1, "aa", 12), new User(1, "aa", 13), new User(3, "aa", 14));
    }
}
