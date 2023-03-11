package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Pattern;
import java.util.Date;

@Value
@Builder
public class UserPaymentsDto {
    Long id;
    @Pattern(regexp = "^0*?[1-9]\\d*$\n")
    Double amount;
    Date paymentDate;
}
