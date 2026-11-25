package org.example.auction.dto.auction;

import java.util.Date;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateAuctionDto {
    Date auctionDate;
    String auctionState;
}
