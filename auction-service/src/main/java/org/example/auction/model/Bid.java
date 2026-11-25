package org.example.auction.model;

import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
