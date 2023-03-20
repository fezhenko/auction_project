package org.example.auction.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.auction.validator.ValidateItemStatuses;

@Value
@Builder
@Jacksonized
public class UpdateItemDto {
    String description;
    @ValidateItemStatuses
    String itemStatus;
    Double price;
    Long itemCategory;
}
