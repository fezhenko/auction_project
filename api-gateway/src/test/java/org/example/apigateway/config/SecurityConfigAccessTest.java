package org.example.apigateway.config;

import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.apigateway.support.UsersServiceStubs.ADMIN_EMAIL;
import static org.example.apigateway.support.UsersServiceStubs.PASSWORD;
import static org.example.apigateway.support.UsersServiceStubs.USER_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pins the gateway's access rules as they are on Spring Security 5, so the move to Security 6 can show it kept them.
 * Callers authenticate the way clients do: a token from {@link Jwt} in the Authorization header, which JwtFilter
 * resolves through the stubbed users-service lookup. "LOGIN" means Spring Security denied an anonymous caller, which
 * form login answers with a redirect to /login. 403 means Spring Security denied an authenticated caller. Any other
 * status is the handler's answer, 404 where the gateway has no handler.
 *
 * <p>MockMvc never runs the servlet ERROR dispatch. In Tomcat a 403 or 404 is forwarded to /error and passes the
 * security chain again. Spring Security 5 does not authorize that dispatch, but Security 6 does, so a caller who is not
 * authenticated again there gets a redirect to /login instead. {@link #deniedAndUnhandledInContainer} repeats those
 * rows over real HTTP to catch that.
 */
class SecurityConfigAccessTest extends AbstractConfiguration {

    private static final String LOGIN = "LOGIN";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jwt jwt;

    @BeforeEach
    void stubDownstreamServices() {
        UsersServiceStubs.stubUserLookup(ADMIN_EMAIL, "users-service/user-admin.json");
        UsersServiceStubs.stubUserLookup(USER_EMAIL, "users-service/user-user.json");
        UsersServiceStubs.stubVerify(USER_EMAIL, "users-service/verify-user.json");
        UsersServiceStubs.stubFindAllUsers();
        stubFor(get(urlEqualTo("/api/v1/auctions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("auction-service/auctions.json")));
    }

    @ParameterizedTest(name = "{0} {1} as {2} -> {3}")
    @CsvSource({
        // permitAll: the login endpoint answers 202 with a token to anyone
        "POST, /api/v1/auth,           ANONYMOUS, 202",
        "POST, /api/v1/auth,           USER,      202",
        "POST, /api/v1/auth,           ADMIN,     202",
        // ADMIN only. The gateway has no handler for /api/v1/users/42, so ADMIN gets past security into a 404.
        "GET,  /api/v1/users,          ANONYMOUS, LOGIN",
        "GET,  /api/v1/users,          USER,      403",
        "GET,  /api/v1/users,          ADMIN,     200",
        "GET,  /api/v1/users/42,       ANONYMOUS, LOGIN",
        "GET,  /api/v1/users/42,       USER,      403",
        "GET,  /api/v1/users/42,       ADMIN,     404",
        // ADMIN or USER. Bids live under /api/v1/buyers/bids, so nothing handles /api/v1/bids: allowed means 404.
        // With only the roles ADMIN and USER, these rows can't tell this rule apart from anyRequest().authenticated().
        "GET,  /api/v1/bids/1,         ANONYMOUS, LOGIN",
        "GET,  /api/v1/bids/1,         USER,      404",
        "GET,  /api/v1/bids/1,         ADMIN,     404",
        // ADMIN only
        "GET,  /swagger-ui/index.html, ANONYMOUS, LOGIN",
        "GET,  /swagger-ui/index.html, USER,      403",
        "GET,  /swagger-ui/index.html, ADMIN,     200",
        // anyRequest: any authenticated caller
        "GET,  /api/v1/auctions,       ANONYMOUS, LOGIN",
        "GET,  /api/v1/auctions,       USER,      200",
        "GET,  /api/v1/auctions,       ADMIN,     200"
    })
    void accessRule(String method, String path, Caller caller, String expected) throws Exception {
        MockHttpServletRequestBuilder request = request(HttpMethod.valueOf(method), path);
        if (HttpMethod.POST.matches(method)) {
            request.contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\": \"" + USER_EMAIL + "\", \"password\": \"" + PASSWORD + "\"}");
        }
        if (caller != Caller.ANONYMOUS) {
            String email = caller == Caller.ADMIN ? ADMIN_EMAIL : USER_EMAIL;
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateToken(email));
        }

        ResultActions result = mockMvc.perform(request);

        if (LOGIN.equals(expected)) {
            result.andExpect(status().isFound()).andExpect(redirectedUrlPattern("**/login"));
        } else {
            result.andExpect(status().is(Integer.parseInt(expected)));
        }
    }

    @ParameterizedTest(name = "GET {0} as {1} -> {2} in Tomcat")
    @CsvSource({
        "/api/v1/users,          USER,  403",
        "/swagger-ui/index.html, USER,  403",
        "/api/v1/users/42,       ADMIN, 404",
        "/api/v1/bids/1,         USER,  404"
    })
    void deniedAndUnhandledInContainer(String path, Caller caller, int expected) {
        String token = jwt.generateToken(caller == Caller.ADMIN ? ADMIN_EMAIL : USER_EMAIL);

        tomcat().get()
                .uri(path)
                .headers(headers -> headers.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isEqualTo(expected);
    }

    enum Caller {
        ANONYMOUS,
        USER,
        ADMIN
    }
}
