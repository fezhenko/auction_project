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
@Table("sellers")
@Builder
public class Seller {
    @Id
    @NotNull
    @Column("id")
    Long sellerId;
    @NotNull
    @Column("auction_id")
    Long auctionId;
    @Column("email")
    @Email
    String email;
    @Column("created_at")
    Date createdAt;
}
