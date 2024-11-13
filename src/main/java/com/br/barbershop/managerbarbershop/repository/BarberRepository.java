package com.br.barbershop.managerbarbershop.repository;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberRepository extends JpaRepository<BarberEntity, Integer> {
    @Query(value = """
            SELECT
              B.BARBER_ID AS barberId,
              B.BARBER_NAME AS barberName,
              L.LOCATION_NAME AS barberLocation,
              L.LOCATION_COMPANY_NAME AS barberCompany,
              L.LOCATION_NUMBER AS barberLocationNumber
            FROM TB_BARBERS B
              JOIN TB_BARBER_LOCATION L ON L.LOCATION_ID = B.BARBER_LOCATION_ID
              JOIN TB_VALUES_SHEET V ON V.BARBER_ID = B.BARBER_ID
              JOIN TB_SERVICES S ON S.SERVICE_ID = V.SERVICE_ID
            WHERE
              B.BARBER_NAME LIKE :barber
              OR L.LOCATION_NAME LIKE :locationName
              OR S.SERVICE_TYPE_NAME LIKE :serviceName
            GROUP BY
              B.BARBER_ID, B.BARBER_NAME, L.LOCATION_NAME, L.LOCATION_NUMBER, L.LOCATION_COMPANY_NAME
            ORDER BY B.BARBER_NAME, L.LOCATION_NAME DESC
          """, nativeQuery = true)
    Page<BarberServicesProjection> findBarbersByFilters(@Param("barber") String barberName,
                                                        @Param("locationName") String locationName,
                                                        @Param("serviceName") String serviceName,
                                                        Pageable pageable);
}
