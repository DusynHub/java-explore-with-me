package ru.practicum.ewm.util.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DomainLengthValidator implements ConstraintValidator<DomainLength, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null || value.isBlank()) {
            return true;
        }

        int domainStart = value.indexOf('@');

        if (domainStart < 0) {
            domainStart = 0;
        }

        String domainPart = value.substring(domainStart + 1);
        String[] domains = domainPart.split("\\.");
        for (String domain : domains) {
            if (domain.length() > 63) {
                return false;
            }
        }
        return true;
    }
}
