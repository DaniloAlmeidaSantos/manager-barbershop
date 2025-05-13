package com.br.barbershop.managerbarbershop.infra.exceptions;

public class ScheduleServicesException extends RuntimeException {

    public ScheduleServicesException(String message, Throwable ex) {
        super(message, ex);
    }

}
