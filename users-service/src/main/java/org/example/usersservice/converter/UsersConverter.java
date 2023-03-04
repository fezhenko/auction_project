package org.example.usersservice.converter;

import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.model.AppUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UsersConverter {
    List<AppUserDto> toDto(List<AppUser> appUsers);

//        public List<AppUserDto> toDto(List<AppUser> appUsers) {
//            List<AppUserDto> usersIds = new ArrayList<>();
//            for (AppUser appUser : appUsers) {
//                usersIds.add(AppUserDto.builder().userId(appUser.getId()).build());
//            }
//            return usersIds;

}
