package com.br.barbershop.managerbarbershop.infra.exceptions;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super("Rate limit error: " + message);
    }

}
