package com.br.barbershop.managerbarbershop.controller;

import com.br.barbershop.managerbarbershop.domain.barber.BarberServicesProjection;
import com.br.barbershop.managerbarbershop.domain.service.GetServicesRequestDTO;
import com.br.barbershop.managerbarbershop.service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(value = "/v1/barber")
public class BarberController {

    private final BarberService barberService;

    @PostMapping("/find")
    public Page<BarberServicesProjection> findBarbers(@RequestBody GetServicesRequestDTO request) {
        return barberService.findBarbers(request);
    }
}
