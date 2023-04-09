package org.example.apigateway.dto.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class AddCurrencyDto {
    @NotNull
    @JsonProperty("currency") String currencyKey;
    @JsonProperty("currencyDescription") String currencyValue;

}
