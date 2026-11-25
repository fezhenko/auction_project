package org.example.usersservice.dto.users;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdatePasswordDto {

    @Pattern(regexp = ".*\\d.*", message = "must contain at least one numeric character")
    @Size(min = 6, max = 32, message = "must have between 6 and 32 characters")
    String password;
}
