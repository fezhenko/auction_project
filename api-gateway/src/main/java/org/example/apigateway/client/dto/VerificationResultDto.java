package org.example.apigateway.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class VerificationResultDto {
    boolean isValid;
}
