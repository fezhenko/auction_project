package org.example.auction.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Builder
@Table("buyers")
public class Buyer {
    @NotNull
    @Id
    @Column("id")
    Long id;
    @NotNull
    @Column("bid_id")
    Long bidId;
    @NotNull
    @Column("auction_id")
    Long auctionId;
    Date createdAt;
}
