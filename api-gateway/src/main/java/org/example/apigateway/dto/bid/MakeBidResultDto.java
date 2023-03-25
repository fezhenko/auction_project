package org.example.apigateway.dto.bid;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class MakeBidResultDto {
    String message;
}
