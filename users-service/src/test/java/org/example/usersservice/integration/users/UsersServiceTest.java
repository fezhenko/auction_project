package org.example.usersservice.integration.users;

import lombok.val;
import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.dto.users.UpdateBalanceResultDto;
import org.example.usersservice.repository.UsersRepository;
import org.example.usersservice.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = UsersServiceTestConfig.class)
class UsersServiceTest {

    @MockBean
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
                .when(userRepository).updateUserBalanceAfterAuctionFinish(appUserDto.getId(), appUserDto.getBalance());

        val actualResult = usersService.updateUserBalance(appUserDto.getId(), userType, price);
        val expectedResult = UpdateBalanceResultDto.builder().balance(1500d).build();
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
