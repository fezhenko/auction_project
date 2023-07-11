package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

@Value
@Jacksonized
@Builder
public class AuctionItemDto {
    @Nullable
    Long id;
    Long seller;
}
