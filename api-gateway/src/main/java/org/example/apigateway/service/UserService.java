package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.client.dto.UserVerificationDto;
import org.example.apigateway.client.dto.VerificationResultDto;
import org.example.apigateway.dto.CreateUserDto;
import org.example.apigateway.dto.CreateUserResultDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    public VerificationResultDto verifyUser(CredentialsDto credentials) {
        UserVerificationDto userVerification = userClient.verifyUserByCredentials(credentials);
        if (passwordEncoder.matches(credentials.getPassword(),
                userVerification.getPassword())) {
            return VerificationResultDto.builder().isValid(true).build();
        }
        return VerificationResultDto.builder().isValid(false).build();
    }

    public CreateUserResultDto createUser(CreateUserDto createUserDto) {
        CreateUserDto userWithEncodedPassword = CreateUserDto.builder()
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .role(createUserDto.getRole()).build();
        userClient.createUser(userWithEncodedPassword);
        return CreateUserResultDto.builder().build();
    }

    public AppUserDto findUserByEmail(String email) {
        return userClient.findUserByEmail(email);
    }

    public List<AppUserDto> findAllUsers() {
        return userClient.findAllUsers();
    }
}
