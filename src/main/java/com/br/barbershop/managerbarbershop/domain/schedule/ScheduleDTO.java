package com.br.barbershop.managerbarbershop.domain.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime finishTime;
}
