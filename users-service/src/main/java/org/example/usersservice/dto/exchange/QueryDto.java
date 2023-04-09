package org.example.usersservice.dto.exchange;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
