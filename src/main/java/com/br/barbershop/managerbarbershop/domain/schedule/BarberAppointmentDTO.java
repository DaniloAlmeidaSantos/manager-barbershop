package com.br.barbershop.managerbarbershop.domain.schedule;

import java.time.LocalDateTime;

/**
 * Projection used by barber management endpoints to expose appointment details,
 * including the associated customer information.
 */
public record BarberAppointmentDTO(
        Integer id,
        Integer customerId,
        String customerName,
        String customerPhone,
        String services,
        LocalDateTime startTime,
        LocalDateTime finishTime,
        String status
) {}
