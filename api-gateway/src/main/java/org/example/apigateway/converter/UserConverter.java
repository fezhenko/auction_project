package org.example.apigateway.converter;

import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserConverter {
    List<UserDto> toDto(List<AppUserDto> users);
    UserDto toDto(AppUserDto user);
}
