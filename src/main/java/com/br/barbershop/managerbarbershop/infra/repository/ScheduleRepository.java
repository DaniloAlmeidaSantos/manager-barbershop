package com.br.barbershop.managerbarbershop.infra.repository;

import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleEntity;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {

    @Query("SELECT s FROM ScheduleEntity s " +
            "JOIN s.barber b " +
            "WHERE b.id = :barberId " +
            "AND DATE(s.startTime) = :startDate")
    List<ScheduleEntity> findByBarberIdAndStartDate(@Param("barberId") Integer barberId, @Param("startDate") Date startDate);

    @Query(value = """
                SELECT
                    DATE(SCHEDULE_START_TIME) AS scheduleDate,
                    TIME(SCHEDULE_START_TIME) AS startTime,
                    TIME(SCHEDULE_FINISH_TIME) AS finishTime
                FROM
                    TB_SCHEDULE
                WHERE
                    SCHEDULE_STATUS = "A"
                    AND DATE(SCHEDULE_START_TIME) BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL :days DAY)
                ORDER BY
                    scheduleDate, startTime
            """, nativeQuery = true)
    List<ScheduleDTO> findOccupiedSchedules(@Param("days") int days);

    /**
     * Returns appointments for a barber filtered by one or more statuses, ordered by start time.
     * Used by barber management endpoints to list current (A/P) or historical (C/F) appointments.
     */
    @Query("SELECT s FROM ScheduleEntity s WHERE s.barber.id = :barberId AND s.statusEnum IN :statuses ORDER BY s.startTime ASC")
    List<ScheduleEntity> findByBarberIdAndStatuses(
            @Param("barberId") Integer barberId,
            @Param("statuses") List<ScheduleStatusEnum> statuses);
}
