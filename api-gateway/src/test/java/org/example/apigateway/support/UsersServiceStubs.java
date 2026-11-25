package org.example.apigateway.support;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

/**
 * WireMock stubs for the users-service endpoints the gateway calls to log a user in and to authenticate a request.
 * The bodies live in {@code wiremock/__files/users-service} and follow the JSON that users-service's UsersController
 * returns. Every password hash in them is the BCrypt hash of {@link #PASSWORD}, a test-only value.
 */
public final class UsersServiceStubs {

    public static final String ADMIN_EMAIL = "admin@auction.test";
    public static final String USER_EMAIL = "user@auction.test";
    public static final String PASSWORD = "test-password";

    private static final String USERS_PATH = "/api/v1/users";

    private UsersServiceStubs() {}

    /** Stubs the lookup JwtFilter makes through AuthService: {@code GET /api/v1/users/validate?email=}. */
    public static void stubUserLookup(String email, String bodyFile) {
        stubFor(get(urlPathEqualTo(USERS_PATH + "/validate"))
                .withQueryParam("email", equalTo(email))
                .willReturn(json(bodyFile)));
    }

    /** Stubs the password check behind {@code POST /api/v1/auth}: {@code POST /api/v1/users/verify}. */
    public static void stubVerify(String email, String bodyFile) {
        stubFor(post(urlEqualTo(USERS_PATH + "/verify"))
                .withRequestBody(matchingJsonPath("$.email", equalTo(email)))
                .willReturn(json(bodyFile)));
    }

    /** Stubs the user list behind {@code GET /api/v1/users}. */
    public static void stubFindAllUsers() {
        stubFor(get(urlEqualTo(USERS_PATH)).willReturn(json("users-service/users.json")));
    }

    private static ResponseDefinitionBuilder json(String bodyFile) {
        return aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile(bodyFile);
    }
}
