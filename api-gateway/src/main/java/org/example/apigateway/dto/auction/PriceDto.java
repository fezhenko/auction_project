package org.example.apigateway.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class PriceDto {
    Double startPrice;
    Double currentPrice;
    Double finalPrice;
    Double minimalBid;
}
