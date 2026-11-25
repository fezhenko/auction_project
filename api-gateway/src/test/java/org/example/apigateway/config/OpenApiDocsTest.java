package org.example.apigateway.config;

import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.example.apigateway.support.UsersServiceStubs.USER_EMAIL;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** springdoc serves the gateway's OpenAPI document in the full context. Like any other path, it needs a token. */
class OpenApiDocsTest extends AbstractConfiguration {

    private static final String AUCTION_PROPERTIES = "$.components.schemas.AuctionDto.properties";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jwt jwt;

    @BeforeEach
    void stubUserLookup() {
        UsersServiceStubs.stubUserLookup(USER_EMAIL, "users-service/user-user.json");
    }

    @Test
    void apiDocsAreServed() throws Exception {
        apiDocs()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(startsWith("3.")))
                .andExpect(jsonPath("$.paths['/api/v1/auth']").exists());
    }

    /**
     * The schema names the properties as the JSON does. AuctionDto renames four of its fields with
     * {@code @JsonProperty}, and swagger-core reads those through Jackson.
     */
    @Test
    void schemaUsesJsonPropertyNames() throws Exception {
        apiDocs()
                .andExpect(status().isOk())
                .andExpect(jsonPath(AUCTION_PROPERTIES, aMapWithSize(7)))
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".id").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".status").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".item").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".prices").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".auctionDate").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".isPayed").exists())
                .andExpect(jsonPath(AUCTION_PROPERTIES + ".createdAt").exists());
    }

    private ResultActions apiDocs() throws Exception {
        return mockMvc.perform(
                get("/v3/api-docs").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateToken(USER_EMAIL)));
    }
}
