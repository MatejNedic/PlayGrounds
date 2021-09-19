package com.mvp.mvp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsDividableBy5Validator implements
        ConstraintValidator<IsDividableBy5, Long> {

    @Override
    public void initialize(IsDividableBy5 price) {
    }

    @Override
    public boolean isValid(Long contactField,
                           ConstraintValidatorContext cxt) {
        return contactField % 5 == 0;
    }

}