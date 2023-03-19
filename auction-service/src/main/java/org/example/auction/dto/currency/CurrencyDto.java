package org.example.auction.dto.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.relational.core.mapping.Embedded;

import javax.validation.constraints.NotNull;


@Builder
@Value
@Jacksonized
public class CurrencyDto {
    @NotNull
    @JsonProperty("currency") String currencyKey;
    @Embedded.Nullable
    @JsonProperty("currencyDescription") String currencyValue;
}
