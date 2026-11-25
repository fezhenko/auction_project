package org.example.usersservice.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CreateUserDto {
    @NotNull
    @Email
    String email;

    @NotNull
    String password;

    @NotNull
    String role;
}
