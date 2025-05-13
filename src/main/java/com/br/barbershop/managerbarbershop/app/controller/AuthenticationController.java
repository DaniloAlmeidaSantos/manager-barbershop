package com.br.barbershop.managerbarbershop.app.controller;

import com.br.barbershop.managerbarbershop.app.annotations.RateLimitProtection;
import com.br.barbershop.managerbarbershop.domain.user.JwtRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;
import com.br.barbershop.managerbarbershop.infra.security.JwtHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtHelper helper;

    private final AuthenticationManager authenticationManager;

    @RateLimitProtection
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody JwtRequestDTO request) {
        var auth = this.doAuthenticate(request);
        JwtResponseDTO response = helper.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(response);
    }

    private Authentication doAuthenticate(JwtRequestDTO request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(request.username(), request.password());
        try {
            return authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException ex) {
            log.error("Error to AUTHENTICATE user to generate JWT TOKEN: {} ", ex.getMessage());
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

}
