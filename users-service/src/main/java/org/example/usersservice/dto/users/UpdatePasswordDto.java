package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Value
@Builder
@Jacksonized
public class UpdatePasswordDto {

    @Pattern(regexp = ".*\\d.*", message = "must contain at least one numeric character")
    @Size(min = 6, max = 32, message = "must have between 6 and 32 characters")
    String password;
}
