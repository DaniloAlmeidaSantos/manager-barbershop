package com.br.barbershop.managerbarbershop.infra.exceptions;

public class BarberNotFoundException extends RuntimeException {

    public BarberNotFoundException(Integer barberId) {
        super("Barbeiro não encontrado com id: " + barberId);
    }
}
