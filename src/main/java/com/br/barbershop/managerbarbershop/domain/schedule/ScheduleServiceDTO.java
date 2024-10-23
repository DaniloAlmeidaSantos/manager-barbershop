package com.br.barbershop.managerbarbershop.domain.schedule;

import java.time.LocalDateTime;
import java.util.Set;

public record ScheduleServiceDTO(Set<Integer> services, Integer customer, Integer barber, String startTime, String finishTime) {
}
