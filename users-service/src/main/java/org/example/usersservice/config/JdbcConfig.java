package org.example.usersservice.config;

import java.sql.Connection;
import java.util.Objects;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfig {
    @SneakyThrows
    @Bean
    public Connection connection(JdbcTemplate jdbcTemplate) {
        return Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
    }
}
