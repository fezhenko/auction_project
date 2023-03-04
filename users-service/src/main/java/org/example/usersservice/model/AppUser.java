package org.example.usersservice.model;

import lombok.Value;

import java.util.Date;

@Value
public class AppUser {
    Long id;
    String firstname;
    String lastname;
    String email;
    String password;
    String role;
    Double balance;
    String phoneNumber;
    Date createdAt;

}
