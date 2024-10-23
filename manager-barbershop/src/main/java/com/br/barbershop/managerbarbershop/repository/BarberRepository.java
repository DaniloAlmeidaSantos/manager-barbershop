package com.br.barbershop.managerbarbershop.repository;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberRepository extends JpaRepository<BarberEntity, Integer> {
}
