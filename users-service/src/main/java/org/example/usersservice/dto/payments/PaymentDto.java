package org.example.usersservice.dto.payments;

import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentDto {

    Long id;
    Long userId;
    String cardNumber;
    String expirationDate;

    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;

    Date paymentDate;
}
