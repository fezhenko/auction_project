package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserValidationResultDto {
    boolean isValid;

}
