package org.example.auction.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Value
@Builder
@Table("auctions")
public class Auction {
    @Id
    @Column("auction_id")
    @NotNull
    Long auctionId;
    @Column("auction_date")
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    Date auctionDate;
    @Column("auction_state")
    String auctionState;
    @Column("seller_id")
    Long sellerId;
    @Column("item_id")
    Long itemId;
    @Column("start_price")
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double startPrice;
    @Column("current_price")
    Double currentPrice;
    @Column("minimal_bid")
    Double minimalBid;
    @Column("final_price")
    Double finalPrice;
    @Column("buyer_id")
    Long buyerId;
    @Column("created_at")
    Date createdAt;
    @Column("last_updated")
    Date lastUpdated;
    @Column("is_payed")
    Boolean isPayed;
}
