package com.br.barbershop.managerbarbershop.app.service.impl;

import com.br.barbershop.managerbarbershop.app.service.BarbershopConfigService;
import com.br.barbershop.managerbarbershop.domain.config.CreateConfigRequestDTO;
import com.br.barbershop.managerbarbershop.infra.exceptions.ConfigException;
import com.br.barbershop.managerbarbershop.infra.repository.BarbershopConfigRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BarbershopConfigServiceImpl implements BarbershopConfigService {

    private final BarbershopConfigRepository barbershopConfigRepository;

    public BarbershopConfigServiceImpl(BarbershopConfigRepository barbershopConfigRepository) {
        this.barbershopConfigRepository = barbershopConfigRepository;
    }

    @Override
    @Transactional(rollbackOn = ConfigException.class)
    public void createConfig(CreateConfigRequestDTO request) {
        try {
            barbershopConfigRepository.saveAndFlush(request.convertToEntity());
            log.info("Config {} created successfully.", request.configName());
        } catch (Exception ex) {
            log.error("Error creating barbershop config: {}", ex.getMessage());
            throw new ConfigException("Error to create config: " + request.configName());
        }
    }
}
