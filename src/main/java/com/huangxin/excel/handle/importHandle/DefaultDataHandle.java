package com.huangxin.excel.handle.importHandle;


import com.huangxin.excel.annotation.ExtConfig;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * DefaultDataHandle
 *
 * @author 黄鑫
 */
public class DefaultDataHandle implements DataHandle {

    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    @Override
    public <T> boolean rowProcess(T t, int rowNum, List<String> msgList, Class<?>... groups) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR.validate(t, groups);
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<T> cv : validateResult) {
                msgList.add(cv.getMessage());
            }
        }
        return true;
    }

    @Override
    public <T> void fieldProcess(T t, Field field, Object fieldValue, List<String> msgList) {
        Optional.ofNullable(field.getAnnotation(ExtConfig.class))
                .flatMap(extConfig -> Optional.ofNullable(extConfig.replace()))
                .ifPresent(strings -> Arrays.stream(strings)
                        .map(s -> s.split("_"))
                        .filter(split -> split.length == 2 && split[1].equals(fieldValue))
                        .map(split -> split[0])
                        .forEach(s -> {
                            try {
                                field.set(t, s);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }));

    }

}
