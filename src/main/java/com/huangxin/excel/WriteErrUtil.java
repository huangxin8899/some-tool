package com.huangxin.excel;

import cn.hutool.core.collection.CollUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
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
     * @param headNum    表头行数索引.
     * @param errColl    错误信息集合.
     * @param row        行号函数.
     * @param errMsg     错误信息函数.
     * @param <T>        错误信息对象.
     * @return 上传文件的URL.
     */
    public static <T> String writeErrMsgAndUpload(File sourceFile, int sheetIndex, int headNum, Collection<T> errColl, Function<T, Integer> row, Function<T, ?> errMsg) {
        if (CollUtil.isNotEmpty(errColl)) {
            Map<Integer, ?> errMap = errColl.stream().collect(Collectors.toMap(row, errMsg));
            return writeErrMsgAndUpload(sourceFile, sheetIndex, headNum, errMap);
        }
        return null;
    }

    // 用poi在excel文件的对应行末尾加入错误信息，且防止OOM
    public static String writeErrMsgAndUpload(File excelFile, int sheetIndex, int headNum, Map<Integer, ?> errorMap) {
        if (!errorMap.isEmpty()) {
            try {
                try (FileInputStream fileInputStream = new FileInputStream(excelFile);
                     SXSSFWorkbook workbook = new SXSSFWorkbook((XSSFWorkbook) WorkbookFactory.create(fileInputStream))) {
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


                    Sheet sheet = workbook.getXSSFWorkbook().getSheetAt(sheetIndex);
                    Row headRow = sheet.getRow(headNum - 1);
                    short lastCellNum = headRow.getLastCellNum();
                    Cell cell = headRow.createCell(lastCellNum);
                    cell.setCellValue("验证不通过原因（重新导入时请务必删除此列）");
                    cell.setCellStyle(cellStyle);
                    sheet.setColumnWidth(lastCellNum, 18000);

                    errorMap.forEach((key, object) -> {
                        String errMsg = null;
                        if (object instanceof Collection) {
                            errMsg = ((Collection<String>) object).stream().collect(Collectors.joining(";", "", ";"));
                        } else if (object instanceof String) {
                            errMsg = ((String) object);
                        } else {
                            throw new RuntimeException("错误信息必须为 String 或者 List<String>.");
                        }
                        Row errorRow = sheet.getRow(key - 1);
                        Cell errorCell = errorRow.createCell(lastCellNum);
                        errorCell.setCellValue(errMsg);
                        errorCell.setCellStyle(cellStyle);
                    });

                    try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                        workbook.write(fos);
                    }

                    // 上传
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
