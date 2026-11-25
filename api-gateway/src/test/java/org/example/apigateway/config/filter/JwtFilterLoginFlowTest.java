package org.example.apigateway.config.filter;

import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.apigateway.client.dto.AuthResultDto;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.example.apigateway.support.UsersServiceStubs.ADMIN_EMAIL;
import static org.example.apigateway.support.UsersServiceStubs.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The login flow inside the gateway: {@code POST /api/v1/auth} checks the password against the hash users-service
 * returns and answers with a token, and JwtFilter then accepts that token on an ADMIN-only endpoint. This also
 * exercises jjwt end to end, signing and parsing, on whatever JDK and classpath the build runs.
 */
class JwtFilterLoginFlowTest extends AbstractConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void stubUsersService() {
        UsersServiceStubs.stubVerify(ADMIN_EMAIL, "users-service/verify-admin.json");
        UsersServiceStubs.stubUserLookup(ADMIN_EMAIL, "users-service/user-admin.json");
        UsersServiceStubs.stubFindAllUsers();
    }

    @Test
    void tokenIssuedByLoginIsAcceptedByJwtFilter() throws Exception {
        // given: the admin logs in with the password whose hash users-service holds
        String token = login();

        // when: the token is sent on an ADMIN-only endpoint
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then: JwtFilter authenticated the caller as ADMIN, so the request reached users-service
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(ADMIN_EMAIL));
        verify(getRequestedFor(urlPathEqualTo("/api/v1/users/validate")).withQueryParam("email", equalTo(ADMIN_EMAIL)));
    }

    @Test
    void tokenWithAlteredSignatureIsRejected() throws Exception {
        // given: a token from the login flow with one signature character changed. The changed character sits
        // inside the signature, because the low bits of the last base64url character may be ignored when decoding.
        String token = login();
        int index = token.lastIndexOf('.') + 10;
        char changed = token.charAt(index) == 'A' ? 'B' : 'A';
        String forged = token.substring(0, index) + changed + token.substring(index + 1);

        // when: the forged token is sent on an ADMIN-only endpoint
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + forged))
                // then: JwtFilter does not authenticate the caller, so form login sends it to /login
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() throws Exception {
        // given: a well-formed HS256 token for the admin, signed with a key the gateway doesn't have (test-only value)
        String foreign = Jwts.builder()
                .subject(ADMIN_EMAIL)
                .signWith(
                        Keys.hmacShaKeyFor("test-only-key-the-gateway-never-saw".getBytes(StandardCharsets.UTF_8)),
                        Jwts.SIG.HS256)
                .compact();

        // when: it is sent on an ADMIN-only endpoint
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + foreign))
                // then: JwtFilter does not authenticate the caller, so neither the user lookup nor the endpoint runs
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
        verify(0, getRequestedFor(urlPathEqualTo("/api/v1/users/validate")));
        verify(0, getRequestedFor(urlPathEqualTo("/api/v1/users")));
    }

    @Test
    void loginBodyWithTrailingContentIsRejected() throws Exception {
        // Jackson 3 fails on content after the JSON value; Jackson 2 ignored it and logged the user in.
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + ADMIN_EMAIL + "\", \"password\": \"" + PASSWORD + "\"} x"))
                .andExpect(status().isBadRequest());
    }

    private String login() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + ADMIN_EMAIL + "\", \"password\": \"" + PASSWORD + "\"}"))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = objectMapper.readValue(response, AuthResultDto.class).getToken();
        assertFalse(token.isBlank(), "login must return a token");
        return token;
    }
}
