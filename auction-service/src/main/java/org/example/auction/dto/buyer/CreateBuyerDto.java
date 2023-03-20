package org.example.auction.dto.buyer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Jacksonized
@Builder
public class CreateBuyerDto {
    @NotNull
    Long auctionId;
}
