package org.example.auction.dto.bid;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonPropertyOrder({"id", "amount", "owner", "createdAt"})
public class BidDto {
    @JsonProperty("id")
    Long bidId;

    @JsonProperty("amount")
    Double bidAmount;

    @JsonProperty("owner")
    Long buyerId;

    Date createdAt;
}
