package org.example.auction.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuctionStateValidator implements ConstraintValidator<ValidateAuctionRelatedStatuses, String> {
    @Override
    public boolean isValid(String auctionState, ConstraintValidatorContext constraintValidatorContext) {
        List<String> auctionStates = Arrays.asList("PLANNED", "IN_PROGRESS", "FINISHED");
        return auctionStates.contains(auctionState.toUpperCase());
    }
}
