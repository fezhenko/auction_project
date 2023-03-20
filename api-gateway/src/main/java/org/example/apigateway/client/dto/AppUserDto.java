package org.example.apigateway.client.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import java.util.Date;

@Value
@Jacksonized
@Builder
public class AppUserDto {
    Long userId;
    String firstname;
    String lastname;
    @Email
    String email;
    String password;
    String role;
    Double balance;
    String phoneNumber;
    Date createdAt;
}
