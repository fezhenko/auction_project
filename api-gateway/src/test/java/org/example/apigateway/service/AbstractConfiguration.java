package org.example.apigateway.service;

import java.net.http.HttpClient;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Full gateway context. auction-service and users-service are replaced by one WireMock server on a random port,
 * so every Feign client talks to stubs and never to a live service. Stubs are reset before each test method, and
 * response bodies come from {@code wiremock/__files} on the test classpath. MockMvc runs requests through the real
 * filter chain, Spring Security and JwtFilter included, and {@link #tomcat()} sends them through Tomcat.
 *
 * <p>The test classes share one cached context, and with it one WireMock server, which wiremock-spring-boot ties to
 * the context. Resetting the stubs and the request journal before every test method keeps the tests independent.
 *
 * <p>The JWT secret is a test-only value set as an inline test property. That outranks a JWT_SECRET exported in the
 * shell, so the tests never sign with an operator's real secret, and a short one can't fail them.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "jwt.secret=test-only-dummy-jwt-secret-not-for-production")
@EnableWireMock(
        @ConfigureWireMock(
                baseUrlProperties = {"services.auction-service.url", "services.users-service.url"},
                filesUnderClasspath = "wiremock"))
@AutoConfigureMockMvc
public abstract class AbstractConfiguration {

    @LocalServerPort
    private int port;

    /**
     * Returns a client for the gateway's Tomcat port. It never follows redirects, so a redirect to the login page
     * shows up as a redirect instead of as the login page.
     */
    protected RestTestClient tomcat() {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        return RestTestClient.bindToServer(new JdkClientHttpRequestFactory(httpClient))
                .baseUrl("http://localhost:" + port)
                .build();
    }
}
