package org.example.usersservice.dto.exchange;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CurrenciesResultDto {
    boolean success;
    Object symbols;
}
