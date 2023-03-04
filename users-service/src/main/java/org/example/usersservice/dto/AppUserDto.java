package org.example.usersservice.dto;

import lombok.Value;

import java.util.Date;

@Value
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
