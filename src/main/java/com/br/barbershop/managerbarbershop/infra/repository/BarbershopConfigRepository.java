package com.br.barbershop.managerbarbershop.infra.repository;

import com.br.barbershop.managerbarbershop.domain.config.BarbershopConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarbershopConfigRepository extends JpaRepository<BarbershopConfigEntity, Integer> {

    /*
        * Find configs to barber with this conditions:
        * 1) If exists barberId in table TB_CONFIG the first priority to get config for this condition
        * 2) If not exists barberId in table TB_CONFIG get config for locationId existence in table FK
     */
    @Query(value = """
            SELECT C.CONFIG_VALUE
                FROM TB_CONFIG C
                JOIN TB_BARBERS B ON B.BARBER_ID = :id
                WHERE C.CONFIG_NAME = :config
                  AND (
                       (C.BARBER_ID = B.BARBER_ID AND C.LOCATION_ID = B.BARBER_LOCATION_ID)
                    OR (C.BARBER_ID IS NULL AND C.LOCATION_ID = B.BARBER_LOCATION_ID)
                  )
                ORDER BY
                  CASE
                    WHEN C.BARBER_ID IS NOT NULL THEN 1
                    ELSE 2
                  END
                LIMIT 1
        """, nativeQuery = true)
    Optional<String> findConfigValueToBarber(@Param("id") long barberId, @Param("config") String config);
}
