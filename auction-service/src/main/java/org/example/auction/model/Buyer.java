package org.example.auction.model;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Builder
@Table("buyers")
public class Buyer {
    @NotNull
    @Id
    @Column("id")
    Long id;

    @Column("bid_id")
    Long bidId;

    @NotNull
    @Column("auction_id")
    Long auctionId;

    @Column("created_at")
    Date createdAt;

    @Column("email")
    @Email
    String email;
}
