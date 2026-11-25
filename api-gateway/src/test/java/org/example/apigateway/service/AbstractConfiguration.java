package org.example.apigateway.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Full gateway context. auction-service and users-service are replaced by one WireMock server on a random port,
 * so every Feign client talks to stubs and never to a live service. Stubs are reset after each test method.
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
@ActiveProfiles("test")
public abstract class AbstractConfiguration {}
