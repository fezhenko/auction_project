package org.example.auction.dto.auction;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateAuctionItemDto {
    @NotNull
    Long itemId;
}
