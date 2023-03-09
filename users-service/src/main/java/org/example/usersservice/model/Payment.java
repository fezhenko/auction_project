package org.example.usersservice.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("payments")
@Value
public class Payment {
    @Id
    @Column("payment_id")
    Long id;
    @Column("user_id")
    Long userId;
    @Column("card_number")
    String cardNumber;
    @Column("expiration_date")

    String expirationDate;
    @Column("amount")
    Double amount;
    @Column("payment_date")
    Date paymentDate;

}
