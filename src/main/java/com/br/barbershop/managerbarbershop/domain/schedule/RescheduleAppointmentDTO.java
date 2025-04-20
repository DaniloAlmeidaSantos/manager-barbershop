package com.br.barbershop.managerbarbershop.domain.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record RescheduleAppointmentDTO(int appointmentId, LocalDate scheduleDate, LocalTime startTime, LocalTime finishTime){}
