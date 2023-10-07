package com.huangxin.excel.handle.importHandle;


import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * DefaultDataHandle
 *
 * @author 黄鑫
 */
public class DefaultDataHandle implements DataHandle {

    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    @Override
    public <T> boolean rowProcess(T t, int rowNum, List<String> msgList) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR.validate(t);
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<T> cv : validateResult) {
                msgList.add(cv.getMessage());
            }
        }
        return true;
    }
}
