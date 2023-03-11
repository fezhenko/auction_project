package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Jacksonized
@Builder
public class CreateUserDto {
    @Size(max = 20)
    @NotNull String email;
    @Size(min = 6, max = 15) String password;
    @NotNull String role;
}
