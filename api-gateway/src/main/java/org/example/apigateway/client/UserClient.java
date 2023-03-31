package org.example.apigateway.client;

import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.UserVerificationDto;
import org.example.apigateway.dto.CreateUserDto;
import org.example.apigateway.dto.UpdateBalanceResultDto;
import org.example.apigateway.dto.auction.FinalPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;


@FeignClient(name = "users-service", url = "${services.users-service.url}/api/v1/users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.POST, value = "/verify")
    UserVerificationDto verifyUserByCredentials(@RequestBody @Valid CredentialsDto credentials);

    @RequestMapping(method = RequestMethod.POST)
    void createUser(@RequestBody @Valid CreateUserDto userWithEncodedPassword);

    @RequestMapping(method = RequestMethod.GET, value = "/validate")
    AppUserDto findUserByEmail(@RequestParam(value = "email") String email);

    @RequestMapping(method = RequestMethod.GET)
    List<AppUserDto> findAllUsers();

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/balance",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    UpdateBalanceResultDto updateUserBalance(@PathVariable("userId") Long userId,
                                             @RequestParam(value = "userType") String userType,
                                             @RequestBody @Valid FinalPriceDto finalPrice);
}
