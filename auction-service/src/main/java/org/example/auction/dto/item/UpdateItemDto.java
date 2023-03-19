package org.example.auction.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.auction.validator.ValidateAuctionRelatedStatuses;

@Value
@Builder
@Jacksonized
public class UpdateItemDto {
    String description;
    @ValidateAuctionRelatedStatuses
    String itemStatus;
    Double price;
    Long itemCategory;
}
