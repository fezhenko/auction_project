package org.example.apigateway.client;

import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.UserVerificationDto;
import org.example.apigateway.dto.CreateUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@FeignClient(name = "users-service", url = "${services.users-service.url}/api/v1/users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.POST, value = "/verify")
    UserVerificationDto verifyUserByCredentials(@RequestBody @Valid CredentialsDto credentials);

    @RequestMapping(method = RequestMethod.POST)
    void createUser(@RequestBody @Valid CreateUserDto userWithEncodedPassword);

    @RequestMapping(method = RequestMethod.GET, value = "/{email}")
    AppUserDto findUserByEmail(@PathVariable("email") String email);
}
