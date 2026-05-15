package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.customer.CustomerDTO;

public interface CustomerService {
    void createUser(CustomerDTO customer);
}
