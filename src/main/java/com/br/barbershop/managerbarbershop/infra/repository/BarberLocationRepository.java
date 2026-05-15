package com.br.barbershop.managerbarbershop.infra.repository;

import com.br.barbershop.managerbarbershop.domain.barber.BarberLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberLocationRepository extends JpaRepository<BarberLocationEntity, Integer> {
}
