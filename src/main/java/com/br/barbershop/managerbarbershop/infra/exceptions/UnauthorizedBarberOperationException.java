package com.br.barbershop.managerbarbershop.infra.exceptions;

public class UnauthorizedBarberOperationException extends RuntimeException {

    public UnauthorizedBarberOperationException() {
        super("Apenas barbeiros com perfil ADMIN podem cadastrar novos barbeiros.");
    }
}
