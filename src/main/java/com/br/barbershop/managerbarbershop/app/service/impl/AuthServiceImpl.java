package com.br.barbershop.managerbarbershop.app.service.impl;

import com.br.barbershop.managerbarbershop.app.service.AuthService;
import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerEntity;
import com.br.barbershop.managerbarbershop.domain.user.CustomerLoginRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;
import com.br.barbershop.managerbarbershop.infra.exceptions.AuthenticateException;
import com.br.barbershop.managerbarbershop.infra.exceptions.BarberNotFoundException;
import com.br.barbershop.managerbarbershop.infra.repository.BarberRepository;
import com.br.barbershop.managerbarbershop.infra.repository.CustomerRepository;
import com.br.barbershop.managerbarbershop.infra.security.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtHelper jwtHelper;

    private final AuthenticationManager barberAuthManager;

    private final AuthenticationManager customerAuthManager;

    private final BarberRepository barberRepository;

    private final CustomerRepository customerRepository;

    public AuthServiceImpl(
            JwtHelper jwtHelper,
            @Qualifier("barberAuthManager") AuthenticationManager barberAuthManager,
            @Qualifier("customerAuthManager") AuthenticationManager customerAuthManager,
            BarberRepository barberRepository,
            CustomerRepository customerRepository) {
        this.jwtHelper = jwtHelper;
        this.barberAuthManager = barberAuthManager;
        this.customerAuthManager = customerAuthManager;
        this.barberRepository = barberRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public JwtResponseDTO loginBarber(JwtRequestDTO request) {
        Authentication auth = authenticate(barberAuthManager, request.username(), request.password());

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .orElse("BARBER_EMPLOYEE");

        BarberEntity barber = barberRepository.findBySystemUserUsername(request.username())
                .orElseThrow(() -> new BarberNotFoundException(null));

        log.info("Barber '{}' authenticated with role {}.", request.username(), role);
        return jwtHelper.generateBarberToken(request.username(), role, barber.getId());
    }

    @Override
    public JwtResponseDTO loginCustomer(CustomerLoginRequestDTO request) {
        authenticate(customerAuthManager, request.email(), request.password());

        CustomerEntity customer = customerRepository.findByEmail(request.email());

        log.info("Customer '{}' authenticated.", request.email());
        return jwtHelper.generateCustomerToken(request.email(), customer.getId());
    }

    private Authentication authenticate(AuthenticationManager manager, String principal, String credentials) {
        try {
            return manager.authenticate(new UsernamePasswordAuthenticationToken(principal, credentials));
        } catch (BadCredentialsException ex) {
            log.warn("Failed authentication attempt for '{}'.", principal);
            throw new AuthenticateException("Credenciais inválidas.");
        }
    }
}
