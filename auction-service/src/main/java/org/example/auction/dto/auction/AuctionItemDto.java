package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.relational.core.mapping.Embedded;

@Value
@Jacksonized
@Builder
public class AuctionItemDto {
    @Embedded.Nullable
    Long id;
    @Embedded.Nullable
    String description;
}
