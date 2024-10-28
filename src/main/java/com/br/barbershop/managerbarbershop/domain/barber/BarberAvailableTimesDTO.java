package com.br.barbershop.managerbarbershop.domain.barber;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class BarberAvailableTimesDTO {
    private Map<LocalDate, List<LocalTime>> availableTimes;
}
