package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import org.springframework.data.domain.Page;

/**
 * Responsible solely for barber query operations.
 * Configuration management is handled by BarbershopConfigService (ISP).
 */
public interface BarberService {
    Page<BarberServicesProjection> findBarbers(GetServicesRequestDTO request); // TODO: Pensar melhor nessa funcionalidade
}
