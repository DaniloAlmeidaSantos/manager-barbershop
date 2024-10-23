package com.br.barbershop.managerbarbershop.repository;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.service.ServicesCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicesCostRepository extends JpaRepository<ServicesCostEntity, Integer> {
    List<ServicesCostEntity> findByBarberId(BarberEntity barberId);
}
