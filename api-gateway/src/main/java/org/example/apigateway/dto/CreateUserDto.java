package org.example.apigateway.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
