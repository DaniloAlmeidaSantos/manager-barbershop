package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.user.CustomerLoginRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;

public interface AuthService {
    /**
     * Authenticates a barber by username/password and returns a signed JWT
     * containing the barber's role (BARBER_ADMIN or BARBER_EMPLOYEE) and barberId.
     */
    JwtResponseDTO loginBarber(JwtRequestDTO request);

    /**
     * Authenticates a customer by email/password and returns a signed JWT
     * containing the CUSTOMER role and customerId.
     */
    JwtResponseDTO loginCustomer(CustomerLoginRequestDTO request);
}
