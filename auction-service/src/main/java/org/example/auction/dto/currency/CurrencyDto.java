package org.example.auction.dto.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Embedded;

import javax.validation.constraints.NotNull;


@Data
public class CurrencyDto {
    @NotNull
    @JsonProperty("currency") String currencyKey;
    @Embedded.Nullable
    @JsonProperty("currencyDescription") String currencyValue;
}
