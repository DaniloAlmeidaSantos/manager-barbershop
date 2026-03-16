package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.config.CreateConfigRequestDTO;

/**
 * Responsible solely for barbershop configuration management.
 * Segregated from BarberService to respect the Interface Segregation Principle —
 * clients that only query barbers should not depend on configuration operations.
 */
public interface BarbershopConfigService {
    void createConfig(CreateConfigRequestDTO request);
}
