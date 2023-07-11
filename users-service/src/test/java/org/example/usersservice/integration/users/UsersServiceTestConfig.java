package org.example.usersservice.integration.users;

import org.example.usersservice.repository.UsersRepository;
import org.example.usersservice.service.UsersService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UsersServiceTestConfig {
    @Bean
    public UsersService usersService(UsersRepository userRepository) {
        return new UsersService(userRepository);
    }
}
