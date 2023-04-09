package org.example.usersservice.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ItemStatusValidator.class)
public @interface ValidateItemStatuses {
    String message() default "Invalid status, allowed values: 'ON_SELL', 'SOLD'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
