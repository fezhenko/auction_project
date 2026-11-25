package org.example.auction.model;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("chat")
public class Chat {
    @Id
    @Column("id")
    @NotNull
    Long id;

    @Column("message")
    @Size(max = 500)
    String message;

    @NotNull
    @Column("buyer_id")
    Long buyerId;

    @NotNull
    @Column("seller_id")
    Long sellerId;

    @Column("created_at")
    Date createdAt;
}
