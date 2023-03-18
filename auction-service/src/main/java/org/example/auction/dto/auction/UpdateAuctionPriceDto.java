package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Builder
@Jacksonized
@Value
public class UpdateAuctionPriceDto {
    @NotNull
    Double currentPrice;
}
