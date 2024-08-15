package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateDateAfterStandard.class)
public @interface DateAfterStandard {
    String message() default "Дата выпуска фильма не может быть раньше {standardDate}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String standardDate() default "1895-12-28";
}