package com.br.barbershop.managerbarbershop.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberAvailableTimesDTO;

public interface AvailableScheduleSlotsService {
    BarberAvailableTimesDTO getAvailableSlots(long barber);
}
