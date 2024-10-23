package com.br.barbershop.managerbarbershop.domain.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerDTO(
        int id,
        String name,
        String phone,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String secret,
        String email
) {

    public CustomerDTO withSecret(String password) {
        return new CustomerDTO(id(), name(), phone(), password, email());
    }

    public CustomerEntity convertToEntity() {
        return new CustomerEntity(id(), name(), phone(), this.secret, email());
    }
}
