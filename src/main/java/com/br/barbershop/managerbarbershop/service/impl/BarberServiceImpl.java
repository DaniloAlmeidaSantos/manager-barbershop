package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import com.br.barbershop.managerbarbershop.repository.BarberRepository;
import com.br.barbershop.managerbarbershop.service.BarberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BarberServiceImpl implements BarberService {

    // TODO: Planejar como retornar dados de serviços para o front conforme barbeiro selecionado

    private final BarberRepository barberRepository;

    private BarberServiceImpl(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Override
    public Page<BarberServicesProjection> findBarbers(GetServicesRequestDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return barberRepository.findBarbersByFilters(
                convertToLikeString(request.getBarberName()),
                convertToLikeString(request.getLocationName()),
                convertToLikeString(request.getServiceName()),
                pageable
        );
    }

    private String convertToLikeString(String field) {
        if (field == null) {
            return null;
        }

        return "%" + field + "%";
    }
}
