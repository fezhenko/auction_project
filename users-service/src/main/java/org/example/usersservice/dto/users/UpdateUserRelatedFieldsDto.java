package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
