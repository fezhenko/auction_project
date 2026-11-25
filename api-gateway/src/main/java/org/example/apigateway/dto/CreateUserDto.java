package org.example.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CreateUserDto {
    @Email
    @NotNull
    String email;

    @NotNull
    String password;

    @NotNull
    String role;
}
