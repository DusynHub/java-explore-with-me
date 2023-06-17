package ru.practicum.ewm.util.annotation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = DomainLengthValidator.class)
@Documented
public @interface DomainLength {

    String message() default "{Domain part length is too long}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
