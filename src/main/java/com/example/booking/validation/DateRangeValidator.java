package com.example.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startField = constraintAnnotation.start();
        this.endField = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
            LocalDate startDate = (LocalDate) beanWrapper.getPropertyValue(startField);
            LocalDate endDate = (LocalDate) beanWrapper.getPropertyValue(endField);

            if (startDate == null || endDate == null) {
                return true; // Use @NotNull on fields to enforce presence
            }

            return endDate.isAfter(startDate);

        } catch (Exception e) {
            return false;
        }
    }
}
