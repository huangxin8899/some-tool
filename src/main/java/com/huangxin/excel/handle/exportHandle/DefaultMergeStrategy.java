package com.huangxin.excel.handle.exportHandle;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.WorkbookWriteHandler;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.huangxin.excel.annotation.ExtConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.*;

/**
 * CustomMergeStrategy
 *
 * @author 黄鑫
 */
public class DefaultMergeStrategy extends AbstractMergeStrategy implements WorkbookWriteHandler, SheetWriteHandler {

    private List<Integer> cols;
    private final Map<Integer, MergeParam> mergeMap = new HashMap<>();
    private Integer sheetNo;

    public DefaultMergeStrategy() {
    }

    public DefaultMergeStrategy(List<Integer> cols) {
        this.cols = cols;
    }

    /**
     * 获取合并列的索引集合
     *
     * @param exportClass 导出类
     */
    private List<Integer> getMergeIndexList(Class<?> exportClass) {
        List<Field> list = new ArrayList<>(1000);
        List<Integer> indexList = new ArrayList<>();
        Arrays.stream(exportClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(ExcelIgnore.class))
                .forEach(field -> {
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    if (excelProperty == null || excelProperty.index() == -1) {
                        list.add(field);
                    }
                });
        Arrays.stream(exportClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(ExcelIgnore.class))
                .forEach(field -> {
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    if (excelProperty != null && excelProperty.index() != -1) {
                        list.add(excelProperty.index(), field);
                    }
                });
        list.forEach(field -> {
            if (field.isAnnotationPresent(ExtConfig.class) && field.getAnnotation(ExtConfig.class).autoMerge()) {
                indexList.add(list.indexOf(field));
            }
        });
        return indexList;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (this.cols == null) {
            Class<?> headClazz = writeSheetHolder.excelWriteHeadProperty().getHeadClazz();
            if (headClazz == null) {
                throw new RuntimeException("请指定自动合并的列索引");
            }
            this.cols = this.getMergeIndexList(headClazz);
        }

    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        for (Integer colNum : cols) {
            if (Objects.equals(colNum, cell.getColumnIndex())) {
                int rowIndex = cell.getRowIndex();
                String currentCellValue = cell.toString();
                MergeParam mergeParam = getMergeParam(colNum, cell);
                if (!mergeParam.getCellValue().equals(currentCellValue)) {
                    if (mergeParam.getStart() != mergeParam.getEnd()) {
                        CellRangeAddress mergedRegion =
                                new CellRangeAddress(mergeParam.getStart(), mergeParam.getEnd(), colNum, colNum);
                        sheet.addMergedRegion(mergedRegion);
                        mergeMap.put(colNum, new MergeParam(currentCellValue, rowIndex, rowIndex));
                    }
                } else {
                    mergeParam.setEnd(rowIndex);
                }
            }
        }

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        this.sheetNo = writeSheetHolder.getSheetNo();
    }

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        if (ObjectUtil.isNotEmpty(mergeMap)) {
            mergeMap.forEach((colNum, mergeParam) -> {
                if (ObjectUtil.isNotEmpty(mergeParam) && mergeParam.getStart() != mergeParam.getEnd()) {
                    CellRangeAddress mergedRegion =
                            new CellRangeAddress(mergeParam.getStart(), mergeParam.getEnd(), colNum, colNum);
                    writeWorkbookHolder.getWorkbook().getSheetAt(sheetNo).addMergedRegion(mergedRegion);
                }
            });
        }
    }

    private MergeParam getMergeParam(Integer colNum, Cell cell) {
        String currentCellValue = cell.toString();
        int rowIndex = cell.getRowIndex();
        MergeParam mergeParam = this.mergeMap.get(colNum);
        if (ObjectUtil.isEmpty(mergeParam)) {
            mergeParam = new MergeParam(currentCellValue, rowIndex, rowIndex);
            this.mergeMap.put(colNum, mergeParam);
        }
        return mergeParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MergeParam {
        private String cellValue;
        private int start;
        private int end;

    }
}
