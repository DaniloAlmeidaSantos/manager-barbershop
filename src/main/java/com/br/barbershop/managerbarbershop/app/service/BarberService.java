package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.config.CreateConfigRequestDTO;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import org.springframework.data.domain.Page;

public interface BarberService {
    Page<BarberServicesProjection> findBarbers(GetServicesRequestDTO request); // TODO: Pensar melhor nessa funcionalidade

    void createConfig(CreateConfigRequestDTO request);
}
