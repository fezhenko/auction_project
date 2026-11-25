package org.example.apigateway.dto.currency;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class CurrencyDto {
    @NotNull
    @JsonProperty("currency")
    String currencyKey;

    @JsonProperty("currencyDescription")
    String currencyValue;
}
