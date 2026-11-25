package org.example.auction.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * auction-service exposes only the health and info actuator endpoints, and health reports only the overall status. The
 * context has Boot's auto-configuration and the service's application.yaml, but none of the service's own beans, so it
 * needs no PostgreSQL: the database health check and the JDBC repositories are off. testcontainers-postgres adds the
 * tests against a real database.
 */
@SpringBootTest(
        classes = ActuatorExposureTest.AutoConfigurationOnly.class,
        properties = {"management.health.db.enabled=false", "spring.data.jdbc.repositories.enabled=false"})
@AutoConfigureMockMvc
class ActuatorExposureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthReportsOnlyTheOverallStatus() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\": \"UP\"}", JsonCompareMode.STRICT));
    }

    @Test
    void infoIsExposed() throws Exception {
        mockMvc.perform(get("/actuator/info")).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/configprops", "/actuator/heapdump"})
    void otherEndpointsAreNotExposed(String path) throws Exception {
        mockMvc.perform(get(path)).andExpect(status().isNotFound());
    }

    /** Boot's auto-configuration alone, without a component scan of the service. */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class AutoConfigurationOnly {}
}
