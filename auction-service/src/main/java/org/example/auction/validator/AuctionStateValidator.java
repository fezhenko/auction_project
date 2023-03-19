package org.example.auction.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class AuctionStateValidator implements ConstraintValidator<ValidateAuctionState, String> {
    @Override
    public boolean isValid(String auctionState, ConstraintValidatorContext constraintValidatorContext) {
        List<String> auctionStates = Arrays.asList("PLANNED", "IN_PROGRESS", "FINISHED");
        return auctionStates.contains(auctionState.toUpperCase());
    }
}
