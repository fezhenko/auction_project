package org.example.apigateway.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class CredentialsDto {
    String email;
    String password;
}
