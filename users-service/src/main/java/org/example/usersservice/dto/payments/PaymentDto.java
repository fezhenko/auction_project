package org.example.usersservice.dto.payments;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.DecimalMin;
import java.util.Date;

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
