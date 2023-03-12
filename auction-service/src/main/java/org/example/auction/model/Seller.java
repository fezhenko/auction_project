package org.example.auction.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Value
@Table("sellers")
public class Seller {
    @Id
    @NotNull
    @Column("id")
    Long id;

    @NotNull
    @Column("auction_id")
    Long auctionId;

}
