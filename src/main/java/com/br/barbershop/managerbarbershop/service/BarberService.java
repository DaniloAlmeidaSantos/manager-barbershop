package com.br.barbershop.managerbarbershop.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesDTO;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesDTO;
import org.springframework.data.domain.Page;

public interface BarberService {
    Page<BarberServicesDTO> findServices(GetServicesDTO request); // TODO: Pensar melhor nessa funcionalidade
}
