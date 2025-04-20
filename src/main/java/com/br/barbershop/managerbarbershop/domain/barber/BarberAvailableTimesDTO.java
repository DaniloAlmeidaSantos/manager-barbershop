package com.br.barbershop.managerbarbershop.domain.barber;

import com.br.barbershop.managerbarbershop.domain.schedule.AvailableTimesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class BarberAvailableTimesDTO {
    private List<AvailableTimesDTO> availableTimes;
}
