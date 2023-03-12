package org.example.auction.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Value
@Table("items")
public class Item {
    @Id
    @Column("id")
    @NotNull
    Long id;
    @Column("description")
    @Size(max = 300)
    String description;

    @Column("price")
    @Min(0)
    @NotNull
    Double price;

    @NotNull
    @Column("currency")
    Integer currency;

    @NotNull
    @Column("item_category")
    Integer itemCategory;

    @NotNull
    @Column("auction_id")
    Long auctionId;

    @NotNull
    @Column("item_state")
    String itemState;

    @NotNull
    @Column("created_at")
    Date createdAt;


}
