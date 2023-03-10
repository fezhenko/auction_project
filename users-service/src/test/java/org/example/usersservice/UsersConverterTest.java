package org.example.usersservice;

import lombok.SneakyThrows;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.dto.users.UserPaymentsDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UsersConverterTest {

    private AppUser appUser;
    private AppUserDto appUserDto;
    private List<AppUser> appUsersList;
    private List<AppUserDto> appUsersDtoList;
    private UsersConverter usersConverter;

    @BeforeEach
    public void init() {
        usersConverter = new UsersConverter() {
            @Override
            public List<AppUserDto> toDto(List<AppUser> appUsers) {
                return appUsers.stream()
                        .map(user -> AppUserDto.builder()
                                .id(user.getId())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .balance(user.getBalance())
                                .phoneNumber(user.getPhoneNumber())
                                .createdAt(user.getCreatedAt())
                                .build()
                        )
                        .collect(Collectors.toList());
            }

            @Override
            public AppUserDto toDto(AppUser appUser) {
                return AppUserDto.builder()
                        .id(appUser.getId())
                        .firstname(appUser.getFirstname())
                        .lastname(appUser.getLastname())
                        .email(appUser.getEmail())
                        .role(appUser.getRole())
                        .balance(appUser.getBalance())
                        .phoneNumber(appUser.getPhoneNumber())
                        .createdAt(appUser.getCreatedAt())
                        .build();
            }

            @Override
            public List<UserPaymentsDto> paymentsToDto(List<Payment> payments) {
                return null;
            }

            @Override
            public UserPaymentsDto paymentsToDto(Payment payments) {
                return null;
            }
        };

        appUsersList = new ArrayList<>();
        appUsersDtoList = new ArrayList<>();
        appUser = new AppUser(
                123123L,
                "firstName",
                "lastName",
                "email@test.test",
                "password1",
                "USER",
                100D,
                "1234567890",
                new Date(3000)
        );
        appUserDto =
                AppUserDto.builder()
                        .id(123123L)
                        .firstname("firstName")
                        .lastname("lastName")
                        .email("email@test.test")
                        .role("USER")
                        .balance(100D)
                        .phoneNumber("1234567890")
                        .createdAt(new Date(3000))
                        .build();
        appUsersList.add(appUser);
        appUsersDtoList.add(appUserDto);
    }

    @Test
    @DisplayName("Verify the user is correctly converted from AppUser to AppUserDto")
    @SneakyThrows
    public void testConvertingFromAppUserToAppUserDto() {
        Assertions.assertEquals(appUserDto, usersConverter.toDto(appUser));
    }

    @Test
    @DisplayName("Verify the list of users is correctly converted from AppUser to AppUserDto")
    @SneakyThrows
    public void testConvertingFromListAppUserToAppUserDto() {
        Assertions.assertEquals(appUsersDtoList, usersConverter.toDto(appUsersList));
    }

}
