package org.example.usersservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidateUserDto {
    String email;
}
