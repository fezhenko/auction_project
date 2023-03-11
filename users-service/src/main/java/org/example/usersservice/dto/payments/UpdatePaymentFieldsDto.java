package org.example.usersservice.dto.payments;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdatePaymentFieldsDto {
    String cardNumber;
    String expirationDate;
}
