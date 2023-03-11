package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class AppUserDto {
    Long id;
    String firstname;
    String lastname;
    String email;
    String role;
    Double balance;
    String phoneNumber;
    Date createdAt;
}
