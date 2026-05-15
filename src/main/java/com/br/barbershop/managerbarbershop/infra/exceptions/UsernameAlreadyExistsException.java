package com.br.barbershop.managerbarbershop.infra.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("Nome de usuário já está em uso: " + username);
    }
}
