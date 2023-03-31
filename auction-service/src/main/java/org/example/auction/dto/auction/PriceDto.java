package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class PriceDto {
    Double startPrice;
    Double minimalBid;
    Double currentPrice;
    Double finalPrice;
    Long buyer;

}
