package org.example.apigateway.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateBalanceResultDto {
    Double balance;
}
