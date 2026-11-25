package org.example.apigateway.converter;

import java.util.List;

import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserConverter {
    List<UserDto> toDto(List<AppUserDto> users);

    UserDto toDto(AppUserDto user);
}
