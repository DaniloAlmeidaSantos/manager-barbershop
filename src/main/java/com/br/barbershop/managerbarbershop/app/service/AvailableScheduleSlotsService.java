package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberAvailableTimesDTO;

public interface AvailableScheduleSlotsService {
    BarberAvailableTimesDTO findAvailableSlots(long barber);
}
