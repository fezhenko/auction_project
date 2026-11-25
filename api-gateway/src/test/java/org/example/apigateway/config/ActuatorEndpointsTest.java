package org.example.apigateway.config;

import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.service.AbstractConfiguration;
import org.example.apigateway.support.UsersServiceStubs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.json.JsonCompareMode;

import static org.example.apigateway.support.UsersServiceStubs.ADMIN_EMAIL;

/**
 * The gateway's actuator endpoints as a container healthcheck sees them, over real HTTP through Tomcat. Health and
 * info answer without a token, health with the overall status only. Nothing else is exposed, even to an ADMIN.
 */
class ActuatorEndpointsTest extends AbstractConfiguration {

    @Autowired
    private Jwt jwt;

    @Test
    void anonymousHealthCheckSeesOnlyTheOverallStatus() {
        tomcat().get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{\"status\": \"UP\"}", JsonCompareMode.STRICT);
    }

    @Test
    void anonymousInfoIsAnswered() {
        tomcat().get().uri("/actuator/info").exchange().expectStatus().isOk();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/configprops", "/actuator/heapdump"})
    void otherEndpointsAreNotExposedEvenToAnAdmin(String path) {
        UsersServiceStubs.stubUserLookup(ADMIN_EMAIL, "users-service/user-admin.json");

        tomcat().get()
                .uri(path)
                .headers(headers -> headers.setBearerAuth(jwt.generateToken(ADMIN_EMAIL)))
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
