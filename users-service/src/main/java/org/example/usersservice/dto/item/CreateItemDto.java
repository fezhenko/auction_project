package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class CreateItemDto {
    String description;
    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double price;
    @NotNull
    Long ownerId;
}
