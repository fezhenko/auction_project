package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class CreateAuctionDto {
    Long sellerId;
}
