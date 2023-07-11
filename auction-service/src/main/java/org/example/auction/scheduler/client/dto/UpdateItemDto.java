package org.example.auction.scheduler.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

@Value
@Builder
@Jacksonized
public class UpdateItemDto {
    @Nullable
    String description;
    @Nullable
    String itemStatus;
    @Nullable
    Double price;
    @Nullable
    Long itemCategory;
}
