package com.br.barbershop.managerbarbershop.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(int service, String barberName) {
        super("Service with id " + service + " not exists to barber: " + barberName);
    }

    public ServiceException(String message) {
        super(message);
    }

}
