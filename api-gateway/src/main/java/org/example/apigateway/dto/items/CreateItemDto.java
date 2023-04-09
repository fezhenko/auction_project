package org.example.apigateway.dto.items;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class CreateItemDto {
    String description;
    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double price;
}
