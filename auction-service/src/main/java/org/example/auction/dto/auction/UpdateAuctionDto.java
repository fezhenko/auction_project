package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Builder
@Jacksonized
public class UpdateAuctionDto {
    Date auctionDate;
    String auctionState;
}
