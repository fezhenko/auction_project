package org.example.apigateway.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.example.apigateway.support.UsersServiceStubs.USER_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controllers act for the caller that JwtFilter authenticated. The user from the token reaches the downstream
 * request: as the owner of a new item, and as the buyer who enrolls in an auction.
 */
class AuthenticatedCallerTest extends AbstractConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jwt jwt;

    @BeforeEach
    void stubDownstreamServices() {
        UsersServiceStubs.stubUserLookup(USER_EMAIL, "users-service/user-user.json");
        stubFor(WireMock.post(urlEqualTo("/api/v1/items"))
                .willReturn(aResponse().withStatus(201)));
        stubFor(WireMock.post(urlEqualTo("/api/v1/buyers"))
                .willReturn(aResponse().withStatus(201)));
    }

    @Test
    void newItemIsOwnedByTheCaller() throws Exception {
        mockMvc.perform(post("/api/v1/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateToken(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Oak chair\", \"price\": 150.0}"))
                .andExpect(status().isCreated());

        // users-service knows user@auction.test as user 2 (user-user.json)
        verify(postRequestedFor(urlEqualTo("/api/v1/items"))
                .withRequestBody(equalToJson("{\"description\": \"Oak chair\", \"price\": 150.0, \"ownerId\": 2}")));
    }

    @Test
    void callerEnrollsAsBuyer() throws Exception {
        mockMvc.perform(post("/api/v1/buyers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateToken(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"auctionId\": 102}"))
                .andExpect(status().isCreated());

        verify(postRequestedFor(urlEqualTo("/api/v1/buyers"))
                .withRequestBody(equalToJson("{\"email\": \"" + USER_EMAIL + "\", \"auctionId\": 102}")));
    }
}
