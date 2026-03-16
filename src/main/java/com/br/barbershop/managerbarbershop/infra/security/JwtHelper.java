package com.br.barbershop.managerbarbershop.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class JwtHelper {

    private static final String ISSUER = "auth-api";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "userId";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expirationTime}")
    private long expirationTime;

    /**
     * Generates a JWT for an authenticated barber.
     * Claims: sub=username, role=BARBER_ADMIN|BARBER_EMPLOYEE, userId=barberId.
     */
    public JwtResponseDTO generateBarberToken(String username, String role, Integer barberId) {
        String token = buildToken(username, role, barberId);
        return JwtResponseDTO.builder().token(token).expirationTime(expirationTime).build();
    }

    /**
     * Generates a JWT for an authenticated customer.
     * Claims: sub=email, role=CUSTOMER, userId=customerId.
     */
    public JwtResponseDTO generateCustomerToken(String email, Integer customerId) {
        String token = buildToken(email, "CUSTOMER", customerId);
        return JwtResponseDTO.builder().token(token).expirationTime(expirationTime).build();
    }

    /**
     * Validates the token signature and expiry, then returns the subject (username/email).
     * Returns an empty string if the token is invalid.
     */
    public String validateToken(String token) {
        try {
            return decode(token).getSubject();
        } catch (JWTVerificationException ex) {
            log.info("JWT validation failed: {}", ex.getMessage());
            return "";
        }
    }

    /**
     * Extracts the role claim from a valid token.
     * Returns an empty string if the token is invalid or the claim is absent.
     */
    public String extractRole(String token) {
        try {
            String role = decode(token).getClaim(CLAIM_ROLE).asString();
            return role != null ? role : "";
        } catch (JWTVerificationException ex) {
            log.info("Failed to extract role from JWT: {}", ex.getMessage());
            return "";
        }
    }

    /**
     * Extracts the userId claim from a valid token.
     * Returns null if the token is invalid or the claim is absent.
     */
    public Integer extractUserId(String token) {
        try {
            return decode(token).getClaim(CLAIM_USER_ID).asInt();
        } catch (JWTVerificationException ex) {
            log.info("Failed to extract userId from JWT: {}", ex.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------------------------

    private String buildToken(String subject, String role, Integer userId) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(subject)
                    .withClaim(CLAIM_ROLE, role)
                    .withClaim(CLAIM_USER_ID, userId)
                    .withExpiresAt(genExpirationDate())
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception ex) {
            log.error("Error generating JWT for subject '{}': {}", subject, ex.getMessage());
            throw new RuntimeException("Error generating JWT token", ex);
        }
    }

    private DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusMinutes(expirationTime).toInstant(ZoneOffset.of("-03:00"));
    }
}
