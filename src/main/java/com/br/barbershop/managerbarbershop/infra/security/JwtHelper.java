package com.br.barbershop.managerbarbershop.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalQuery;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;

@Slf4j
@Component
public class JwtHelper {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expirationTime}")
    private long expirationTime;

    public JwtResponseDTO generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Instant expirationTime = genExpirationDate();
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(expirationTime)
                    .sign(algorithm);

            return JwtResponseDTO.builder()
                    .expirationTime(this.expirationTime) // Get expiration time
                    .token(token)
                    .build();
        } catch (JWTCreationException ex) {
            log.info("Error while generating token: {}", ex.getMessage());
            throw new RuntimeException("Error while generating token", ex);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex){
            log.info("Error in JWT verification: {} ", ex.getMessage());
            return "";
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusMinutes(expirationTime).toInstant(ZoneOffset.of("-03:00"));
    }
}
