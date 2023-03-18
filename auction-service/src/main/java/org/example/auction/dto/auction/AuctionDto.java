package org.example.auction.dto.auction;

import lombok.Data;

import java.util.Date;

@Data
public class AuctionDto {
    String auctionId;
    Date auctionDate;
    String status;
    String itemId;
    String itemDescription;
    Double minimalBid;
    Double startPrice;
    Double currentPrice;
    Double finalPrice;
    Date createdAt;
}
