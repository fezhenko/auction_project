package org.example.auction.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ItemStatusValidator implements ConstraintValidator<ValidateAuctionRelatedStatuses, String> {
    @Override
    public boolean isValid(String itemStatus, ConstraintValidatorContext constraintValidatorContext) {
        List<String> itemStatuses = Arrays.asList("ON_SELL", "SOLD");
        return itemStatuses.contains(itemStatus.toUpperCase());
    }
}
