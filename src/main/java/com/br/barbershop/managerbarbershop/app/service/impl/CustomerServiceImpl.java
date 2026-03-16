package com.br.barbershop.managerbarbershop.app.service.impl;

import com.br.barbershop.managerbarbershop.domain.customer.CustomerDTO;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerEntity;
import com.br.barbershop.managerbarbershop.infra.repository.CustomerRepository;
import com.br.barbershop.managerbarbershop.app.service.CustomerService;
import com.br.barbershop.managerbarbershop.app.utils.EmailValidatorUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final PasswordEncoder passwordEncoder;

    private final CustomerRepository repository;

    @Autowired
    public CustomerServiceImpl(PasswordEncoder passwordEncoder, CustomerRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void createUser(CustomerDTO customer) {
        // TODO: Validate if exists email and phone in database before insert data in table.
        String pwdEncrypted = passwordEncoder.encode(customer.secret());

        if (pwdEncrypted == null) {
            throw new RuntimeException("Error while encrypt password");
        }

        EmailValidatorUtils.isEmailValid(customer.email());
        CustomerEntity entity = customer.withSecret(pwdEncrypted).convertToEntity();
        log.info("Creating customer in database...");
        var result = repository.save(entity);

        if (result == null || result.getId() == 0) {
            throw new RuntimeException("Error while creating user.");
        }

        log.info("Customer {} created success!", customer.name());
    }

}
