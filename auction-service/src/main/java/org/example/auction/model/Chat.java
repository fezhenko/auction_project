package org.example.auction.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

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
