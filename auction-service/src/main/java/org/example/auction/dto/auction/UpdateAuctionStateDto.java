package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.auction.validator.ValidateAuctionState;

import javax.validation.constraints.NotNull;


@Value
@Jacksonized
@Builder
public class UpdateAuctionStateDto {
    @ValidateAuctionState
    @NotNull
    String status;
}
