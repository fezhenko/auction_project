package org.example.auction.validator;

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
@Constraint(validatedBy = AuctionStateValidator.class)
public @interface ValidateAuctionState {
    String message() default "Invalid auction state, use on of values: 'PLANNED','IN_PROGRESS','FINISHED'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
