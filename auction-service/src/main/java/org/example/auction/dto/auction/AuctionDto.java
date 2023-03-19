package org.example.auction.dto.auction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Jacksonized
@Builder
public class AuctionDto {
    Long auctionId;
    Date auctionDate;
    @JsonProperty("status")
    String auctionState;
    @JsonProperty("item")
    AuctionItemDto itemDto;
    @JsonProperty("prices")
    PriceDto priceDto;
    Date createdAt;
}
