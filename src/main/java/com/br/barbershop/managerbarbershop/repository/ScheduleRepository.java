package com.br.barbershop.managerbarbershop.repository;

import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleEntity;
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
}