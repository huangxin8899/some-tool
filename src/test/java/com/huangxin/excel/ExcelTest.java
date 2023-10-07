package com.huangxin.excel;

import com.alibaba.excel.EasyExcel;
import com.huangxin.excel.handle.exportHandle.DefaultMergeStrategy;
import com.huangxin.excel.handle.exportHandle.ReplaceWriteHandle;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * ExcelTest
 *
 * @author 黄鑫
 */
public class ExcelTest {

    @Test
    public void testImport() {
        File file = new File("C:\\Users\\chenh\\Desktop\\新建 XLSX 工作表 (2).xlsx");
        ImportHelper.ofFile(file, User.class, new Limit(), "123")
                .skip(50)
                .limit(21)
                .page(10, users -> System.out.println("+1"));
        System.out.println();
    }

    @Test
    public void testExport() {
        String fileName = "C:\\Users\\chenh\\Desktop\\新建 XLSX 工作表 (3).xlsx";
        EasyExcel.write(fileName, User.class)
                .sheet()
                .registerWriteHandler(new DefaultMergeStrategy())
                .registerWriteHandler(new ReplaceWriteHandle())
                .doWrite(ExcelTest::getList);
    }

    private static List<User> getList() {
        return Arrays.asList(new User(1, "aa", 12), new User(1, "aa", 13), new User(3, "aa", 14));
    }
}
