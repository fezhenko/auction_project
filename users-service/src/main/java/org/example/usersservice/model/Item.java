package org.example.usersservice.model;

import java.util.Date;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("items")
@Builder
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

    @Column("currency")
    Long currency;

    @Column("currencyValue")
    Object currencyDesc;

    @NotNull
    @Column("item_category")
    Long itemCategory;

    @Column("categoryName")
    String categoryName;

    @NotNull
    @Column("item_state")
    String itemState;

    @NotNull
    @Column("created_at")
    Date createdAt;

    @NotNull
    @Column("owner_id")
    Long ownerId;
}
