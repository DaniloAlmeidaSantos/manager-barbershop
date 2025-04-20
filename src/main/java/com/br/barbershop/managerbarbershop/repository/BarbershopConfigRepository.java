package com.br.barbershop.managerbarbershop.repository;

import com.br.barbershop.managerbarbershop.domain.config.BarbershopConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarbershopConfigRepository extends JpaRepository<BarbershopConfigEntity, Integer> {
    @Query(value = """
            SELECT
                C.CONFIG_VALUE
            FROM TB_CONFIG C
              JOIN TB_BARBER_LOCATION L ON L.LOCATION_ID = C.LOCATION_ID
              JOIN TB_BARBERS B ON B.BARBER_LOCATION_ID = C.LOCATION_ID
            WHERE B.BARBER_ID = :id AND C.CONFIG_NAME = :config
        """, nativeQuery = true)
    Optional<String> findConfigValueToBarber(@Param("id") long id, @Param("config") String config);
}
