package com.br.barbershop.managerbarbershop.infra.configurations;

import com.br.barbershop.managerbarbershop.infra.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    private final UserDetailsService barberUserDetailsService;

    private final UserDetailsService customerUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            @Qualifier("authorization-backend") UserDetailsService barberUserDetailsService,
            @Qualifier("customer-auth") UserDetailsService customerUserDetailsService,
            PasswordEncoder passwordEncoder) {
        this.jwtFilter = jwtFilter;
        this.barberUserDetailsService = barberUserDetailsService;
        this.customerUserDetailsService = customerUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------------------------------------------------------------
    // Security filter chain
    // -------------------------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // --- Public endpoints ---
                        .requestMatchers(HttpMethod.POST, "/auth/barber/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/customer/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/bootstrap").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/customer").permitAll()

                        // --- ADMIN-only barber operations (evaluated before the general barber rule) ---
                        .requestMatchers(HttpMethod.POST, "/v1/barber/management/barbers").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/barber/manage" +
                                "" +
                                "ment/locations").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/barber/config").hasRole("BARBER_ADMIN")

                        // --- Any authenticated barber ---
                        .requestMatchers("/v1/barber/management/**").hasAnyRole("BARBER_ADMIN", "BARBER_EMPLOYEE")
                        .requestMatchers("/v1/barber/**").hasAnyRole("BARBER_ADMIN", "BARBER_EMPLOYEE")

                        // --- Customer endpoints ---
                        .requestMatchers("/v1/customer/**").hasRole("CUSTOMER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // -------------------------------------------------------------------------
    // Authentication managers — one per user type
    // -------------------------------------------------------------------------

    /**
     * AuthenticationManager for barbers: validates against TB_SYSTEM_USERS
     * using Argon2 password hashing.
     */
    @Primary
    @Bean("barberAuthManager")
    public AuthenticationManager barberAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(barberUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    /**
     * AuthenticationManager for customers: validates against TB_CUSTOMERS
     * using Argon2 password hashing.
     */
    @Bean("customerAuthManager")
    public AuthenticationManager customerAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
