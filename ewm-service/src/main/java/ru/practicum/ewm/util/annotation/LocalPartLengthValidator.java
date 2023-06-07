package ru.practicum.ewm.util.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocalPartLengthValidator implements ConstraintValidator<LocalPartLength, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null || value.isBlank()) {
            return false;
        }
        int start = value.indexOf('@');
        String localPart = value.substring(0, start + 1);
        return localPart.length() <= 64;
    }
}
