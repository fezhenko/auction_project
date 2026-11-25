package org.example.usersservice.dto.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateUserRelatedFieldsDto {

    @NotNull
    @Pattern(regexp = ".*@.*", message = "must contain @ character")
    @Size(min = 6, max = 32, message = "must have between 6 and 32 characters")
    String email;

    @Size(min = 3, max = 20)
    String firstname;

    @Size(min = 3, max = 20)
    String lastname;

    @Size(min = 6)
    String phoneNumber;
}
