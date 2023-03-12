package org.example.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class CurrencyDto {
    @JsonProperty("currency") String currencyKey;
    @JsonProperty("currencyDescription") String currencyValue;
}
