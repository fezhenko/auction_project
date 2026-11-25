package org.example.auction.dto.auction;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.auction.validator.ValidateAuctionRelatedStatuses;

@Value
@Jacksonized
@Builder
public class UpdateAuctionStateDto {
    @ValidateAuctionRelatedStatuses
    @NotNull
    String status;
}
