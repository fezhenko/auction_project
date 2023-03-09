package org.example.usersservice.dto.payments;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Pattern;
import java.util.Date;

@Value
@Builder
public class PaymentDto {

    Long id;
    Long userId;
    String cardNumber;
    String expirationDate;
    @Pattern(regexp = "^0*?[1-9]\\d*$\n")
    Double amount;
    Date paymentDate;

}
