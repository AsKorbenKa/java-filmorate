package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidateDateAfterStandard implements ConstraintValidator<DateAfterStandard, LocalDate> {
    LocalDate standardDate = LocalDate.MIN;

    @Override
    public void initialize(DateAfterStandard constraintAnnotation) {
        standardDate = LocalDate.parse(constraintAnnotation.standardDate());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        }

        return !localDate.isBefore(standardDate);
    }
}
