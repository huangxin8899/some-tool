package com.huangxin.excel;

import java.time.format.DateTimeFormatter;

/**
 * ExcelConstant
 *
 * @author 黄鑫
 */
public interface ExcelConstant {

    DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * xlsx excel导出后缀
     */
    String XLSX_EXCEL_SUFFIX = ".xlsx";

    String ROW_HANDLE_CLASS_NAME = "com.baseus.common.core.utils.excel.handle.exportHandle.RowHandle";
}
