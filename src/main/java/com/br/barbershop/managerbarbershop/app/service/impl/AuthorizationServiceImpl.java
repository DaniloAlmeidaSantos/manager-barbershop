package com.br.barbershop.managerbarbershop.app.service.impl;

import com.br.barbershop.managerbarbershop.domain.user.SystemUserEntity;
import com.br.barbershop.managerbarbershop.infra.exceptions.AuthenticateException;
import com.br.barbershop.managerbarbershop.infra.repository.SystemUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("authorization-backend")
public class AuthorizationServiceImpl implements UserDetailsService {

    private final SystemUserRepository repository;

    private AuthorizationServiceImpl(SystemUserRepository repository) {
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
        // NOTE: The password stored in TB_SYSTEM_USERS must be an Argon2 hash.
        // DaoAuthenticationProvider calls passwordEncoder.matches(rawInput, storedHash) internally.
        // Do NOT re-encode here — double-hashing would always fail authentication.
        return User.builder()
                .roles(entity.getRole())
                .password(entity.getPassword())
                .username(entity.getUsername())
                .build();
    }
}
