package org.example.auction.scheduler.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class FinalPriceDto {
    Double finalPrice;
}
