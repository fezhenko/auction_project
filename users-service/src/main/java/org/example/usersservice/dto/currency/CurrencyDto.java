package org.example.usersservice.dto.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.relational.core.mapping.Embedded;

@Builder
@Value
@Jacksonized
public class CurrencyDto {
    @NotNull
    @JsonProperty("currency")
    String currencyKey;

    @Embedded.Nullable
    @JsonProperty("currencyDescription")
    String currencyValue;
}
