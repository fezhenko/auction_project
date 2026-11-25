package org.example.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.config.filter.JwtFilter;
import org.example.apigateway.config.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests.requestMatchers("/api/v1/auth")
                        .permitAll()
                        // container healthchecks call these without a token
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info")
                        .permitAll()
                        .requestMatchers("/api/v1/users/**")
                        .hasAnyRole("ADMIN")
                        .requestMatchers("/api/v1/bids/**")
                        .hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/swagger-ui/index.html")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form.withObjectPostProcessor(new AbsoluteLoginRedirect()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(authService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * Makes form login send a caller who is not authenticated to the login page with an absolute URL, as Spring
     * Security 6 did. Since Security 7 the redirect is relative by default. The absolute URL is built from the
     * request's Host header, so a proxy in front of the gateway needs forwarded-header support.
     */
    private static final class AbsoluteLoginRedirect implements ObjectPostProcessor<LoginUrlAuthenticationEntryPoint> {

        @Override
        public <O extends LoginUrlAuthenticationEntryPoint> O postProcess(final O entryPoint) {
            entryPoint.setFavorRelativeUris(false);
            return entryPoint;
        }
    }
}
