package com.br.barbershop.managerbarbershop.service;

import com.br.barbershop.managerbarbershop.domain.schedule.RescheduleAppointmentDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;

public interface ScheduleService {
    void scheduleService(ScheduleServiceDTO payload);
    void rescheduleAppointment(RescheduleAppointmentDTO rescheduleAppointmentDTO);
}
