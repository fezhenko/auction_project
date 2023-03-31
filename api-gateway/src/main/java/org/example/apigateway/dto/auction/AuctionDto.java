package org.example.apigateway.dto.auction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Jacksonized
@Builder
@JsonPropertyOrder({"id", "status", "item", "prices", "auctionDate", "createdAt"})
public class AuctionDto {
    @JsonProperty("id")
    Long auctionId;
    Date auctionDate;
    @JsonProperty("status")
    String auctionState;
    @JsonProperty("item")
    AuctionItemDto itemDto;
    @JsonProperty("prices")
    PriceDto priceDto;
    Boolean isPayed;
    Date createdAt;
}
