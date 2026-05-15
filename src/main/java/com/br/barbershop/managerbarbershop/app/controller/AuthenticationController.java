package com.br.barbershop.managerbarbershop.app.controller;

import com.br.barbershop.managerbarbershop.app.annotations.RateLimitProtection;
import com.br.barbershop.managerbarbershop.app.service.AuthService;
import com.br.barbershop.managerbarbershop.app.service.BarberManagementService;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.CustomerLoginRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtRequestDTO;
import com.br.barbershop.managerbarbershop.domain.user.JwtResponseDTO;
import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    private final BarberManagementService barberManagementService;

    /**
     * Authenticates a barber and returns a signed JWT.
     * Token carries: sub=username, role=BARBER_ADMIN|BARBER_EMPLOYEE, userId=barberId.
     *
     * POST /auth/barber/login
     */
    @RateLimitProtection
    @PostMapping("/barber/login")
    public ResponseEntity<JwtResponseDTO> barberLogin(@RequestBody @Valid JwtRequestDTO request) {
        return ResponseEntity.ok(authService.loginBarber(request));
    }

    /**
     * Authenticates a customer and returns a signed JWT.
     * Token carries: sub=email, role=CUSTOMER, userId=customerId.
     *
     * POST /auth/customer/login
     */
    @RateLimitProtection
    @PostMapping("/customer/login")
    public ResponseEntity<JwtResponseDTO> customerLogin(@RequestBody @Valid CustomerLoginRequestDTO request) {
        return ResponseEntity.ok(authService.loginCustomer(request));
    }

    /**
     * One-time bootstrap endpoint: creates the first ADMIN barber.
     * Only works when TB_BARBERS is empty.
     * The caller's IP must match SERVER_PRINCIPLE_IP in TB_CONFIG.
     * No JWT required — this endpoint is intentionally public.
     *
     * POST /auth/bootstrap
     */
    @PostMapping("/bootstrap")
    public ResponseEntity<ApiResponseDTO> bootstrap(
            @RequestBody @Valid CreateBarberRequestDTO payload,
            HttpServletRequest httpRequest) {

        barberManagementService.bootstrapAdminBarber(payload, extractClientIp(httpRequest));
        return new ResponseEntity<>(
                new ApiResponseDTO(String.valueOf(HttpStatus.CREATED.value()),
                        "Barbeiro ADMIN criado com sucesso. Faça login em /auth/barber/login."),
                HttpStatus.CREATED
        );
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
