package com.br.barbershop.managerbarbershop.domain.user;

import lombok.Builder;

@Builder
public record JwtResponseDTO(String token, long expirationTime) {
}
