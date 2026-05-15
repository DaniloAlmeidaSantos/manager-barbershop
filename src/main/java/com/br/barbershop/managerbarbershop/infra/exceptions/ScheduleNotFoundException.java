package com.br.barbershop.managerbarbershop.infra.exceptions;

public class ScheduleNotFoundException extends RuntimeException {

    public ScheduleNotFoundException(Integer appointmentId) {
        super("Agendamento não encontrado com id: " + appointmentId);
    }
}
