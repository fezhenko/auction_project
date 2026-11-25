package org.example.apigateway.config.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The secret that signs and verifies the gateway's JWTs, bound from {@code jwt.secret}. application.yaml takes it from
 * the JWT_SECRET environment variable.
 *
 * <p>The secret is checked when the key is built, not while the property is bound: a binding failure report would
 * print the value. No message, log line or {@link #toString()} shows it.
 *
 * @param secret the value of JWT_SECRET, empty when the variable is not set
 */
@ConfigurationProperties("jwt")
public record JwtProperties(String secret) {

    /** HMAC-SHA256 needs a key of at least 256 bits. */
    public static final int MIN_SECRET_BYTES = 32;

    /**
     * Returns the HMAC key: the UTF-8 bytes of the secret, used as they are.
     *
     * @throws IllegalStateException when the secret is missing or shorter than {@value #MIN_SECRET_BYTES} bytes
     */
    public SecretKey signingKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException(
                    "JWT_SECRET is not set. Set it to a secret of at least " + MIN_SECRET_BYTES + " bytes in UTF-8.");
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET is too short. It must be at least " + MIN_SECRET_BYTES + " bytes in UTF-8.");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public String toString() {
        return "JwtProperties[secret=(hidden)]";
    }
}
