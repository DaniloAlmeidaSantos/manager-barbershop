package com.br.barbershop.managerbarbershop.infra.repository;

import com.br.barbershop.managerbarbershop.domain.user.SystemUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUserEntity, Integer> {
    SystemUserEntity findByUsername(String username);
}
