package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.user.SystemUserEntity;
import com.br.barbershop.managerbarbershop.exceptions.AuthenticateException;
import com.br.barbershop.managerbarbershop.repository.SystemUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("authorization-backend")
public class AuthorizationServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final SystemUserRepository repository;

    private AuthorizationServiceImpl(PasswordEncoder passwordEncoder, SystemUserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    // TODO: Implement proxy arch with redis
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUserEntity entity = repository.findByUsername(username);

        if (entity == null) {
            throw new AuthenticateException("User not exists in system to generate token.");
        }

        return convertToUserDetails(entity);
    }

    private UserDetails convertToUserDetails(SystemUserEntity entity) {
        return User.builder()
                .roles(entity.getRole())
                .password(passwordEncoder.encode(entity.getPassword()))
                .username(entity.getUsername())
                .build();
    }
}
