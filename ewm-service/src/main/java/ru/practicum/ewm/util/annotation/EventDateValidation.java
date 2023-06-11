package ru.practicum.ewm.util.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface EventDateValidation {

    String message() default "{Date and time for which the event is scheduled cannot be earlier than two hours from the current moment}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
