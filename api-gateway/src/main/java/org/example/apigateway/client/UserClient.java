package org.example.apigateway.client;

import java.util.List;

import jakarta.validation.Valid;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.UserVerificationDto;
import org.example.apigateway.dto.CreateUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service", url = "${services.users-service.url}/api/v1/users")
public interface UserClient {

    @PostMapping("/verify")
    UserVerificationDto verifyUserByCredentials(@RequestBody @Valid CredentialsDto credentials);

    @PostMapping
    void createUser(@RequestBody @Valid CreateUserDto userWithEncodedPassword);

    @GetMapping("/validate")
    AppUserDto findUserByEmail(@RequestParam(value = "email") String email);

    @GetMapping
    List<AppUserDto> findAllUsers();
}
