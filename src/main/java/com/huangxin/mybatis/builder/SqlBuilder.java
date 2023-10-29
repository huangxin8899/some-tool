package com.huangxin.mybatis.builder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.huangxin.mybatis.ConditionType;
import com.huangxin.mybatis.MetaColumn;
import com.huangxin.mybatis.anno.ConditionFlag;
import com.huangxin.mybatis.util.AnnoUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * SqlBuilder
 *
 * @author 黄鑫
 */
public class SqlBuilder {

    public static <Query> SelectBuilder query(Query queryParam) {
        SelectBuilder selectBuilder = new SelectBuilder();
        Class<?> paramClass = queryParam.getClass();
        selectBuilder.select(paramClass).from(paramClass);
        List<Field> fields = AnnoUtil.getFields(paramClass, new ArrayList<>());
        fields.stream().filter(field -> field.isAnnotationPresent(ConditionFlag.class)).forEach(field -> {
            ConditionFlag conditionFlag = field.getAnnotation(ConditionFlag.class);
            String columnName = ObjectUtil.isNotEmpty(conditionFlag.fieldName()) ? conditionFlag.fieldName() : MetaColumn.ofField(field).getColumnName();
            ConditionType type = conditionFlag.type();
            Object fieldValue = ReflectUtil.getFieldValue(queryParam, field);
            selectBuilder.apply(ObjectUtil.isNotEmpty(fieldValue), type, columnName, fieldValue);
        });
        return selectBuilder;
    }
}
