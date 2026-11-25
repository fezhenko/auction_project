package org.example.apigateway.dto.currency;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Value
public class FullCurrencyInfoDto {
    @NotNull
    Long id;

    @NotNull
    CurrencyDto currency;
}
