package org.example.apigateway.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Builder
@Jacksonized
public class AppUserDto {
    Long id;
    String firstname;
    String lastname;
    String password;
    String email;
    String role;
    Double balance;
    String phoneNumber;
    Date createdAt;
}
