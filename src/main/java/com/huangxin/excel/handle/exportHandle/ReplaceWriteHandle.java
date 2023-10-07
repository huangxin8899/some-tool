package com.huangxin.excel.handle.exportHandle;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.huangxin.excel.annotation.ExtConfig;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;
import java.util.*;

/**
 * ReplaceWriteHandle
 *
 * @author 黄鑫
 */
public class ReplaceWriteHandle implements CellWriteHandler, SheetWriteHandler {

    private Map<Integer, List<String>> replaceMap;

    public ReplaceWriteHandle() {
    }

    public ReplaceWriteHandle(Class<?> exportClass) {
        this.replaceMap = getReplaceIndexList(exportClass);
    }

    /**
     * 获取字段转换列的索引集合
     *
     * @param exportClass 导出类
     */
    private Map<Integer, List<String>> getReplaceIndexList(Class<?> exportClass) {
        Map<Integer, List<String>> map = new HashMap<>();
        List<Field> list = new ArrayList<>(1000);
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
            if (field.isAnnotationPresent(ExtConfig.class) && field.getAnnotation(ExtConfig.class).replace() != null) {
                String[] replace = field.getAnnotation(ExtConfig.class).replace();
                map.put(list.indexOf(field), Arrays.asList(replace));
            }
        });
        return map;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (this.replaceMap == null) {
            Class<?> headClazz = writeSheetHolder.excelWriteHeadProperty().getHeadClazz();
            if (headClazz == null) {
                throw new RuntimeException("请指定自动合并的列索引");
            }
            this.replaceMap = this.getReplaceIndexList(headClazz);
        }
    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        replaceMap.forEach((colNum, replacePairs) -> {
            if (Objects.equals(colNum, cell.getColumnIndex())) {
                String oldValue = cellData.getStringValue();
                for (String replacePair : replacePairs) {
                    String[] split = replacePair.split("_");
                    if (split.length == 2 && split[1].equals(oldValue)) {
                        cellData.setStringValue(split[0]);
                        break;
                    }
                }
            }
        });
    }
}
