package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.client.dto.AuthResultDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.VerificationResultDto;
import org.example.apigateway.converter.UserConverter;
import org.example.apigateway.dto.CreateUserDto;
import org.example.apigateway.dto.CreateUserResultDto;
import org.example.apigateway.dto.UserDto;
import org.example.apigateway.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/v1")
@RestController
@Tag(name = "Users")
@AllArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class UserController {
    private final UserService userService;
    private final UserConverter userConverter;
    private final Jwt jwt;

    @Tag(name = "Auth")
    @PostMapping("/auth")
    private ResponseEntity<AuthResultDto> auth(@RequestBody @Valid CredentialsDto credentials) {
        VerificationResultDto isVerified = userService.verifyUser(credentials);
        if (isVerified.isValid()) {
            final String token = jwt.generateToken(credentials.getEmail());
            return ResponseEntity.accepted().body(AuthResultDto.builder().token(token).build());
        }
        return ResponseEntity.badRequest().body(AuthResultDto.builder().token("Invalid credentials").build());
    }

    @Tag(name = "Users")
    @GetMapping("/users")
    private ResponseEntity<List<UserDto>> findAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<AppUserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(userConverter.toDto(users));
    }

    @Tag(name = "Users")
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
