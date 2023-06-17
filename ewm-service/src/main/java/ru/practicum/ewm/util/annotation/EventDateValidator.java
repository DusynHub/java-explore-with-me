package ru.practicum.ewm.util.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateValidation, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime earliestAvailableDate = LocalDateTime.now().plusHours(2);

        if (value != null) {
            return value.isAfter(earliestAvailableDate);
        }
        return true;
    }
}
