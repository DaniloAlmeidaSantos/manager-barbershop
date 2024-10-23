package com.br.barbershop.managerbarbershop.controller;

import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerAuthenticateDTO;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;
import com.br.barbershop.managerbarbershop.service.CustomerService;
import com.br.barbershop.managerbarbershop.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(value = "/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createUser(@RequestBody CustomerDTO payload) {
        customerService.createUser(payload);
        return ResponseEntity.ok(
                new ApiResponseDTO(
                    String.valueOf(HttpStatus.CREATED.value()), "Usuário criado com sucesso!"));
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<CustomerDTO> authenticateCustomer(@RequestBody CustomerAuthenticateDTO payload) {
        CustomerDTO customer = customerService.authenticateUser(payload);
        return ResponseEntity.ok(customer);
    }

    @PostMapping(value = "/schedule/services")
    public ResponseEntity<ApiResponseDTO> scheduleService(@RequestBody ScheduleServiceDTO payload){
        scheduleService.scheduleService(payload);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Serviços agendados com sucesso"));
    }
}