package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidateUserDto {
    String email;
}
