package com.br.barbershop.managerbarbershop.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerLoginRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
