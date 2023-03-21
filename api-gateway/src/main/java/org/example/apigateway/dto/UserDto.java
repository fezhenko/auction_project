package org.example.apigateway.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Jacksonized
@Builder
public class UserDto {
    Long id;
    String firstname;
    String lastname;
    String email;
    String role;
    Double balance;
    String phoneNumber;
    Date createdAt;
}
