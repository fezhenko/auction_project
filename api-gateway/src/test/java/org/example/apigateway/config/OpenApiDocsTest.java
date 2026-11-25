package org.example.apigateway.config;

import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.apigateway.support.UsersServiceStubs.USER_EMAIL;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** springdoc 2 serves the gateway's OpenAPI document in the full context. Like any other path, it needs a token. */
class OpenApiDocsTest extends AbstractConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jwt jwt;

    @Test
    void apiDocsAreServed() throws Exception {
        UsersServiceStubs.stubUserLookup(USER_EMAIL, "users-service/user-user.json");

        mockMvc.perform(get("/v3/api-docs")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateToken(USER_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(startsWith("3.")))
                .andExpect(jsonPath("$.paths['/api/v1/auth']").exists());
    }
}
