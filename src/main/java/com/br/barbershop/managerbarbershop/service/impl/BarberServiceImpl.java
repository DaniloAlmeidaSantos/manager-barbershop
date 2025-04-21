package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.config.CreateConfigRequestDTO;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import com.br.barbershop.managerbarbershop.exceptions.ConfigException;
import com.br.barbershop.managerbarbershop.repository.BarberRepository;
import com.br.barbershop.managerbarbershop.repository.BarbershopConfigRepository;
import com.br.barbershop.managerbarbershop.service.BarberService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BarberServiceImpl implements BarberService {

    // TODO: Planejar como retornar dados de serviços para o front conforme barbeiro selecionado
    private final BarberRepository barberRepository;

    private final BarbershopConfigRepository barbershopConfigRepository;

    public BarberServiceImpl(BarberRepository barberRepository, BarbershopConfigRepository barbershopConfigRepository) {
        this.barberRepository = barberRepository;
        this.barbershopConfigRepository = barbershopConfigRepository;
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

    @Override
    @Transactional(rollbackOn = ConfigException.class)
    public void createConfig(CreateConfigRequestDTO request) {
        try {
            barbershopConfigRepository.saveAndFlush(request.convertToEntity());
        } catch (Exception ex) {
            log.error("Error to creating barbershop config");
            throw new ConfigException("Error to create config: " + request.configName());
        }
    }

    private String convertToLikeString(String field) {
        if (field == null) return null;

        return "%" + field + "%";
    }
}
