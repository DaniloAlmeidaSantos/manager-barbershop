package com.br.barbershop.managerbarbershop.domain.barber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for creating a new barber.
 *
 * - role is never provided by the caller: the service assigns ADMIN (bootstrap) or EMPLOYEE.
 * - username/password are used to create the linked SystemUserEntity for JWT authentication.
 */
public record CreateBarberRequestDTO(
        @NotBlank String name,
        @NotNull Integer locationId,
        @NotBlank String username,
        @NotBlank String password
) {}
