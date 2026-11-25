package org.example.apigateway.config.jwt;

import java.nio.charset.StandardCharsets;

import org.example.apigateway.ApiGatewayApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedExceptionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Starts the whole gateway with different JWT_SECRET values. A secret shorter than 32 bytes in UTF-8, or none, stops
 * startup before Tomcat opens its port, with a message that names JWT_SECRET. Neither the message nor anything the
 * gateway logs contains the secret. The values here are test-only.
 */
@ExtendWith(OutputCaptureExtension.class)
class JwtSecretStartupTest {

    /** 31 bytes. */
    private static final String SHORT_SECRET = "test-only-secret-of-31-bytes-xx";

    /** 16 characters, 32 bytes in UTF-8: the length is counted in bytes, not characters. */
    private static final String SECRET_OF_32_BYTES = "ąćęłńóśźżĄĆĘŁŃÓŚ".substring(0, 16);

    @Test
    void secretShorterThan32BytesFailsStartup(CapturedOutput output) {
        assertThat(SHORT_SECRET.getBytes(StandardCharsets.UTF_8)).hasSize(31);

        assertThatThrownBy(() -> start(SHORT_SECRET))
                .satisfies(failure -> assertThat(NestedExceptionUtils.getMostSpecificCause(failure))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("JWT_SECRET is too short. It must be at least 32 bytes in UTF-8."))
                .satisfies(failure -> assertNoMessageContains(failure, SHORT_SECRET));

        assertThat(output).doesNotContain(SHORT_SECRET).doesNotContain("Tomcat started");
    }

    /** An unset JWT_SECRET reaches the check as an empty value, through application.yaml's {@code ${JWT_SECRET:}}. */
    @Test
    void missingOrEmptySecretFailsStartup(CapturedOutput output) {
        assertThatThrownBy(() -> start(""))
                .satisfies(failure -> assertThat(NestedExceptionUtils.getMostSpecificCause(failure))
                        .hasMessage("JWT_SECRET is not set. Set it to a secret of at least 32 bytes in UTF-8."));

        assertThat(output).doesNotContain("Tomcat started");
    }

    @Test
    void secretOf32BytesStarts(CapturedOutput output) {
        assertThat(SECRET_OF_32_BYTES).hasSize(16);
        assertThat(SECRET_OF_32_BYTES.getBytes(StandardCharsets.UTF_8)).hasSize(32);

        try (ConfigurableApplicationContext context = start(SECRET_OF_32_BYTES)) {
            String token = context.getBean(Jwt.class).generateToken("user@auction.test");

            assertThat(context.getBean(Jwt.class).getLoginFromToken(token)).isEqualTo("user@auction.test");
        }
        assertThat(output).contains("Tomcat started").doesNotContain(SECRET_OF_32_BYTES);
    }

    private static void assertNoMessageContains(Throwable failure, String secret) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            assertThat(String.valueOf(cause.getMessage())).doesNotContain(secret);
        }
    }

    /** The secret goes in as a command-line argument, which outranks application.yaml's JWT_SECRET placeholder. */
    private static ConfigurableApplicationContext start(String secret) {
        return new SpringApplicationBuilder(ApiGatewayApplication.class)
                .properties("server.port=0", "spring.main.banner-mode=off")
                .run("--jwt.secret=" + secret);
    }
}
