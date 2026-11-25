package org.example.usersservice.converter;

import java.util.List;

import javax.validation.Valid;

import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.dto.users.UserPaymentsDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.mapstruct.Mapper;

@Mapper
public interface UsersConverter {

    List<AppUserDto> toDto(@Valid List<AppUser> appUsers);

    AppUserDto toDto(@Valid AppUser appUser);

    List<UserPaymentsDto> paymentsToDto(@Valid List<Payment> payments);

    UserPaymentsDto paymentsToDto(@Valid Payment payments);
}
