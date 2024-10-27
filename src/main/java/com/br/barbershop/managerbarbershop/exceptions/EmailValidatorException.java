package com.br.barbershop.managerbarbershop.exceptions;

import lombok.Getter;

public class EmailValidatorException extends IllegalArgumentException {

    @Getter private String email;

    public EmailValidatorException(String email) {
        super("Invalid e-mail: " + email);
        this.email = email;
    }
}
