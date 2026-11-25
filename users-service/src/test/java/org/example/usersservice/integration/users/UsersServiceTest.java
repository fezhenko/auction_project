package org.example.usersservice.integration.users;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.dto.users.UpdateBalanceResultDto;
import org.example.usersservice.repository.UsersRepository;
import org.example.usersservice.service.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = UsersServiceTestConfig.class)
class UsersServiceTest {

    @MockitoBean
    UsersRepository userRepository;

    @Autowired
    UsersService usersService;

    @Test
    public void userBalanceShouldBeUpdated() {
        AppUserDto appUserDto = AppUserDto.builder()
                .id(111L)
                .balance(1000d)
                .role("USER")
                .email("test@email.com")
                .firstname("test")
                .lastname("test")
                .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC()).minusMonths(36)))
                .phoneNumber(null)
                .build();
        String userType = "seller";
        Double price = 500d;
        Mockito.doReturn(1000d).when(userRepository).findBalanceByUserId(appUserDto.getId());
        Mockito.doReturn(appUserDto.getBalance() + price)
                .when(userRepository)
                .updateUserBalanceAfterAuctionFinish(appUserDto.getId(), appUserDto.getBalance());

        final var actualResult = usersService.updateUserBalance(appUserDto.getId(), userType, price);
        final var expectedResult =
                UpdateBalanceResultDto.builder().balance(1500d).build();
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
