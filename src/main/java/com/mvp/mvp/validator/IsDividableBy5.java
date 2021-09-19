package com.mvp.mvp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsDividableBy5Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsDividableBy5 {
    String message() default "Invalid price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}