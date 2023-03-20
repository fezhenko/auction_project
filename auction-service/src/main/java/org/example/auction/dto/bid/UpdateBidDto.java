package org.example.auction.dto.bid;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateBidDto {
    Double amount;
}
