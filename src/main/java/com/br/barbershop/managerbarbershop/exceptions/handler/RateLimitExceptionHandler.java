package com.br.barbershop.managerbarbershop.exceptions.handler;

import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.exceptions.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitExceptionHandler {
    @ExceptionHandler({RateLimitException.class})
    public ResponseEntity<ApiResponseDTO> handleInvalidFieldsInValidJson(final RateLimitException rateLimitException) {
        log.error(String.format("%s: %s", UUID.randomUUID(), rateLimitException.getMessage()), rateLimitException);
        return new ResponseEntity<>(
                new ApiResponseDTO(HttpStatus.TOO_MANY_REQUESTS.toString(), rateLimitException.getMessage()),
                HttpStatus.TOO_MANY_REQUESTS);
    }
}
