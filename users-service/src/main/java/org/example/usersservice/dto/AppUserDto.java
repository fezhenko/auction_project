package org.example.usersservice.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Builder
public class AppUserDto {
    @NotNull
    Long id;
    String firstname;
    String lastname;
    @NotNull
    String email;
    @NotEmpty
    String role;
    @NotNull
    Double balance;
    String phoneNumber;
    Date createdAt;
}
