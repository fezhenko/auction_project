package org.example.usersservice.dto.users;

import java.util.Date;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserPaymentsDto {
    Long id;

    @Pattern(regexp = "^0*?[1-9]\\d*$\n")
    Double amount;

    Date paymentDate;
}
