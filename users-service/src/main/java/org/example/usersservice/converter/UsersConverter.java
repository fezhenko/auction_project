package org.example.usersservice.converter;

import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.model.AppUser;
import org.mapstruct.Mapper;

import javax.validation.Valid;
import java.util.List;

@Mapper
public interface UsersConverter {

    List<AppUserDto> toDto(@Valid List<AppUser> appUsers);

    AppUserDto toDto(@Valid AppUser appUser);

}
