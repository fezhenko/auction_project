package org.example.auction.dto.bid;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateBidResultDto {
    String message;
}
