package org.example.apigateway.dto.items;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class AddItemDto {
    @NotNull
    Long itemId;
}
