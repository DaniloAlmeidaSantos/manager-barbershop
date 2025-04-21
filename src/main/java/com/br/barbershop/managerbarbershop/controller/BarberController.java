package com.br.barbershop.managerbarbershop.controller;

import com.br.barbershop.managerbarbershop.annotations.RateLimitProtection;
import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.config.CreateConfigRequestDTO;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import com.br.barbershop.managerbarbershop.service.BarberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(value = "/v1/barber")
public class BarberController {

    private final BarberService barberService;

    @RateLimitProtection
    @PostMapping("/find")
    public Page<BarberServicesProjection> findBarbers(@RequestBody GetServicesRequestDTO request) {
        return barberService.findBarbers(request);
    }

    @PostMapping("/config")
    public ResponseEntity<ApiResponseDTO> createConfig(@RequestBody @Valid CreateConfigRequestDTO payload) {
        barberService.createConfig(payload);
        return new ResponseEntity<>(
                new ApiResponseDTO(HttpStatus.CREATED.toString(), "Configuração criada com sucesso"),
                HttpStatus.CREATED
        );
    }
}
