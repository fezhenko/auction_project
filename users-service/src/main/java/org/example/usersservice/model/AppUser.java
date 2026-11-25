package org.example.usersservice.model;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("users")
public class AppUser {
    @Id
    @Column("user_id")
    @NotNull
    Long id;

    @Column("firstname")
    String firstname;

    @Column("lastname")
    String lastname;

    @Column("email")
    @NotNull
    String email;

    @Column("password")
    @NotNull
    String password;

    @Column("role")
    @NotNull
    String role;

    @Column("balance")
    @NotNull
    Double balance;

    @Column("phone_number")
    String phoneNumber;

    @Column("created_at")
    Date createdAt;
}
