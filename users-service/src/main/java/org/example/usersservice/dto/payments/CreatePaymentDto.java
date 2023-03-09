package org.example.usersservice.dto.payments;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @Pattern(regexp = "^0*?[1-9]\\d*$\n")
    Double amount;
}
