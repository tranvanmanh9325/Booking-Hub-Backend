package com.example.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    // Vietnam phone number regex: 10 digits starting with 0
    private static final String PHONE_PATTERN = "^0\\d{9}$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isEmpty()) {
            return true; // Allow null/empty, use @NotBlank if required
        }
        return phone.matches(PHONE_PATTERN);
    }
}
