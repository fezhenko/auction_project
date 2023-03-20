package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.client.dto.AuthResultDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.VerificationResultDto;
import org.example.apigateway.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("api/v1")
@RestController
@Tag(name = "Users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Jwt jwt;

    @GetMapping("/auth")
    private ResponseEntity<AuthResultDto> auth(@RequestBody @Valid CredentialsDto credentials) {
        VerificationResultDto isVerified = userService.verifyUser(
                credentials.getUsername(), credentials.getPassword());
        if (isVerified.isValid()) {
            final String token = jwt.generateToken(credentials.getUsername());
            return ResponseEntity.accepted().body(AuthResultDto.builder().token(token).build());
        }
        return ResponseEntity.badRequest().body(AuthResultDto.builder().token("Invalid credentials").build());
    }
}
