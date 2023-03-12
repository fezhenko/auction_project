package org.example.auction.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Builder
@Table("bids")
public class Bid {
    @Id
    @NotNull
    @Column("bid_id")
    Long bidId;
    @NotNull
    @Column("buyer_id")
    Long buyerId;
    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    @Column("bid_amount")
    Double bidAmount;
    @Column("created_at")
    Date createdAt;

}
