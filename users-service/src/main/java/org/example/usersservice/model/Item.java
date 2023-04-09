package org.example.usersservice.model;

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
