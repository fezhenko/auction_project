package org.example.apigateway.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Full gateway context. auction-service and users-service are replaced by one WireMock server on a random port,
 * so every Feign client talks to stubs and never to a live service. Stubs are reset after each test method.
 * MockMvc runs requests through the real filter chain, Spring Security and JwtFilter included.
 *
 * <p>Each test class gets its own context. Spring Cloud Contract stops WireMock after every test class and restarts it
 * on the same port, while a cached context would keep Feign's pooled keep-alive connections to the stopped server.
 * The next class would then fail its first request with NoHttpResponseException.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
            "services.auction-service.url=http://localhost:${wiremock.server.port}",
            "services.users-service.url=http://localhost:${wiremock.server.port}",
            "wiremock.reset-mappings-after-each-test=true"
        })
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public abstract class AbstractConfiguration {}
