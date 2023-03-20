package org.example.auction.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Table("sellers")
public class Seller {
    @Id
    @NotNull
    @Column("id")
    Long sellerId;

    @NotNull
    @Column("auction_id")
    Long auctionId;

    @Column("created_at")
    Date createdAt;

}
