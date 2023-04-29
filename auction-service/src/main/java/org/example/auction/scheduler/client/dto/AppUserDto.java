package org.example.auction.scheduler.client.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
