package com.huangxin.mybatis.util;

import cn.hutool.core.util.StrUtil;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.constant.SqlConstant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ScriptUtil
 *
 * @author 黄鑫
 */
public class ScriptUtil {

    private static final String UPDATE_SCRIP =
            "<foreach collection=\"list\" item=\"item\" separator=\";\">\n" +
                    "    update {}\n" +
                    "    <set>\n" +
                    "       {}\n" +
                    "    </set>\n" +
                    "    where {}\n" +
                    "</foreach>";

    private static final String IF_SCRIP = "<if test=\"item.{0} != null and item.{0} != ''\">{1}</if>";
    private static final String IF_NUM_SCRIP = "<if test=\"item.{0} != null\">{1}</if>";
    private static final String ITEM = "#{item.{}}";
    private static final String ITEM_COMMA = "#{item.{}},";

    public static String updateBatchByIdScrip(boolean allowNull, Class<?> updateClass) {
        StringBuilder setSb = new StringBuilder();
        StringBuilder whereSb = new StringBuilder();
        String tableName = AnnoUtil.getTableName(updateClass);
        String primaryName = AnnoUtil.getPrimaryName(updateClass);
        String primaryColumn = AnnoUtil.getPrimaryColumn(updateClass);
        whereSb.append(StrUtil.format("{} = {}", primaryColumn, StrUtil.format(ITEM, primaryName)));
        List<Field> fields = AnnoUtil.getFields(updateClass, new ArrayList<>());
        for (int i = 0, fieldsSize = fields.size(), j = 0; i < fieldsSize; i++) {
            Field field = fields.get(i);
            if (!primaryName.equals(field.getName())) {
                String fieldName = field.getName();
                String columnName = MetaColumn.ofField(field).getColumnName();
                String str = i < fieldsSize - 1 ?
                                StrUtil.format("{} = {}", columnName, StrUtil.format(ITEM_COMMA, fieldName)) :
                                StrUtil.format("{} = {}", columnName, StrUtil.format(ITEM, fieldName));
                if (j != 0) {
                    setSb.append("\n       ");
                }
                if (allowNull) {
                    setSb.append(str);
                } else {
                    if (isNumeric(field.getType())) {
                        setSb.append(StrUtil.indexedFormat(IF_NUM_SCRIP, columnName, str));
                    } else {
                        setSb.append(StrUtil.indexedFormat(IF_SCRIP, columnName, str));
                    }
                }
                j++;
            }
        }
        return StrUtil.format(UPDATE_SCRIP, tableName, setSb.toString(), whereSb.toString());
    }

    private static boolean isNumeric(Class<?> cls) {
        return cls.isPrimitive() || Number.class.isAssignableFrom(cls);
    }
}
