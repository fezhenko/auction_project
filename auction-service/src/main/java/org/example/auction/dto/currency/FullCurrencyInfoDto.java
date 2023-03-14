package org.example.auction.dto.currency;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Jacksonized
@Builder
@Value
public class FullCurrencyInfoDto {
    @NotNull
    Long id;
    @NotNull
    CurrencyDto currency;
}
