package org.example.apigateway.dto.buyer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateBuyerResultDto {
    String message;
}
