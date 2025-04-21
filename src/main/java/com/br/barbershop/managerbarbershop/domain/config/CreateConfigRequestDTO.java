package com.br.barbershop.managerbarbershop.domain.config;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.barber.BarberLocationEntity;
import jakarta.validation.constraints.NotNull;

public record CreateConfigRequestDTO (Integer configId,
                                      Integer barberId,
                                      @NotNull String configValue,
                                      @NotNull ConfigNameEnum configName,
                                      @NotNull Integer locationId
) {

    public BarbershopConfigEntity convertToEntity() {
        return BarbershopConfigEntity.builder()
                .id(configId)
                .value(configValue)
                .name(configName.getConfigName())
                .barberId(BarberEntity.builder().id(barberId).build())
                .locationId(BarberLocationEntity.builder().id(locationId).build())
                .build();
    }
}
