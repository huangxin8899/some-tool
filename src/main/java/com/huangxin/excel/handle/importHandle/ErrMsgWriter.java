package com.huangxin.excel.handle.importHandle;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ErrMsgWriter
 *
 * @author 黄鑫
 */
public class ErrMsgWriter implements SheetWriteHandler {

    private final Map<Integer, ?> errorMap;
    private final int titleNum;

    public ErrMsgWriter(Map<Integer, ?> errorMap, int titleNum) {
        this.errorMap = errorMap;
        this.titleNum = titleNum - 1;
    }


    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        CellStyle cellStyle = writeWorkbookHolder.getWorkbook().createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Sheet sheet = writeSheetHolder.getCachedSheet();
        Row titleRow = sheet.getRow(titleNum);
        Cell errMsgCell = titleRow.createCell(titleRow.getLastCellNum());
        errMsgCell.setCellValue("验证不通过原因（重新导入时请务必删除此列）");
        errMsgCell.setCellStyle(cellStyle);
        sheet.setColumnWidth(titleRow.getLastCellNum() - 1, 18000);
        errorMap.forEach((index, object) -> {
            String errMsg = null;
            if (object instanceof Collection) {
                errMsg = ((Collection<String>) object).stream().collect(Collectors.joining(";", "", ";"));
            } else if (object instanceof String) {
                errMsg = ((String) object);
            } else {
                throw new RuntimeException("错误信息必须为 String 或者 List<String>.");
            }
            Row row = sheet.getRow(index - 1);
            Cell cell = row.createCell(titleRow.getLastCellNum() - 1);
            cell.setCellValue(errMsg);
            cell.setCellStyle(cellStyle);
        });

    }

}
