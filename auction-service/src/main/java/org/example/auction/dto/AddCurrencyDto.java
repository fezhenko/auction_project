package org.example.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Jacksonized
@Builder
public class AddCurrencyDto {
    @NotNull
    @JsonProperty("currency") String currencyKey;
    @JsonProperty("currencyDescription") String currencyValue;

}
