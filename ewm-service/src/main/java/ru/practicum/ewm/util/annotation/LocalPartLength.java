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
@Constraint(validatedBy = LocalPartLengthValidator.class)
@Documented
public @interface LocalPartLength {

    String message() default "{Local part length is invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
