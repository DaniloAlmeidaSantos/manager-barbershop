package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;
import jakarta.annotation.Nullable;

public interface ScheduleService {
    void scheduleService(ScheduleServiceDTO payload, @Nullable Integer appointmentId);
}
