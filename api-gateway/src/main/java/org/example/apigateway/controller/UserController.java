package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.client.dto.AuthResultDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.VerificationResultDto;
import org.example.apigateway.dto.CreateUserDto;
import org.example.apigateway.dto.CreateUserResultDto;
import org.example.apigateway.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RequestMapping("api/v1")
@RestController
@Tag(name = "Users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Jwt jwt;

    @PostMapping("/auth")
    private ResponseEntity<AuthResultDto> auth(@RequestBody @Valid CredentialsDto credentials) {
        VerificationResultDto isVerified = userService.verifyUser(credentials);
        if (isVerified.isValid()) {
            final String token = jwt.generateToken(credentials.getEmail());
            return ResponseEntity.accepted().body(AuthResultDto.builder().token(token).build());
        }
        return ResponseEntity.badRequest().body(AuthResultDto.builder().token("Invalid credentials").build());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<CreateUserResultDto> createUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                           @RequestBody @Valid CreateUserDto createUserDto) {
        if (token.isEmpty()) {
            CreateUserResultDto result = CreateUserResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        CreateUserResultDto result = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
