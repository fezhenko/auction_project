package org.example.apigateway.dto.auction;

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
    @Nullable
    String description;
    Long seller;
}
