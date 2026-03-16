package com.br.barbershop.managerbarbershop.domain.barber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBarberLocationRequestDTO(
        @NotBlank String companyName,
        @NotBlank String locationName,
        @NotBlank String postalCode,
        String complement,
        @NotBlank String number,
        @NotNull @Size(min = 1, max = 1) Character state,
        @NotBlank String city
) {
    public BarberLocationEntity toEntity() {
        return BarberLocationEntity.builder()
                .locationCompanyName(companyName)
                .locationName(locationName)
                .postalCode(postalCode)
                .complement(complement)
                .number(number)
                .state(state)
                .city(city)
                .build();
    }
}
