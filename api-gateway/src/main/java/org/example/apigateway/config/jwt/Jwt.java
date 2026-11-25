package org.example.apigateway.config.jwt;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Issues and checks the gateway's JWTs: HMAC-SHA256 with the key from {@link JwtProperties}. */
@Slf4j
@Component
public class Jwt {

    private final SecretKey key;

    /** Builds the signing key at startup, so a missing or short JWT_SECRET stops the gateway from starting. */
    public Jwt(JwtProperties properties) {
        this.key = properties.signingKey();
    }

    public String generateToken(String login) {
        Date date = Date.from(
                LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(login)
                .expiration(date)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.info("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.info("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.info("Malformed jwt");
        } catch (SignatureException sEx) {
            log.info("Invalid signature");
        } catch (JwtException | IllegalArgumentException e) {
            log.info("invalid token");
        }
        return false;
    }

    public String getLoginFromToken(String token) {
        return parse(token).getSubject();
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
