package org.example.usersservice.dto.payments;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreatePaymentDto {

    @NotNull
    Long userId;

    @NotNull
    String cardNumber;

    @NotNull
    @Pattern(regexp = ".*/.*", message = "must contain / character")
    @Size(min = 5, max = 5)
    String expirationDate;

    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;
}
