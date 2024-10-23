package com.br.barbershop.managerbarbershop.service;


import com.br.barbershop.managerbarbershop.domain.customer.CustomerAuthenticateDTO;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerDTO;

public interface CustomerService {
    void createUser(CustomerDTO customer);

    CustomerDTO authenticateUser(CustomerAuthenticateDTO customerAuthenticate);
}
