package com.br.barbershop.managerbarbershop.infra.exceptions;

public class AuthenticateException extends IllegalArgumentException{

    public AuthenticateException() {
        super("Authentication failed!");
    }

    public AuthenticateException(String message) {
        super(message);
    }

}
