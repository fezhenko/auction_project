package org.example.usersservice.dto.exchange;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class QueryDto {
    @NotNull
    @Size(min = 3, max = 4)
    String from;

    @NotNull
    @Size(min = 3, max = 4)
    String to;

    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;
}
