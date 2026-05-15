package com.br.barbershop.managerbarbershop.app.controller;

import com.br.barbershop.managerbarbershop.app.annotations.RateLimitProtection;
import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.domain.barber.BarberAvailableTimesDTO;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;
import com.br.barbershop.managerbarbershop.app.service.AvailableScheduleSlotsService;
import com.br.barbershop.managerbarbershop.app.service.CustomerService;
import com.br.barbershop.managerbarbershop.app.service.ScheduleService;
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

    private final AvailableScheduleSlotsService availableScheduleSlotsService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createUser(@RequestBody CustomerDTO payload) {
        customerService.createUser(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDTO(
                    String.valueOf(HttpStatus.CREATED.value()), "Usuário criado com sucesso!"));
    }

    @PostMapping(value = "/schedule/service")
    public ResponseEntity<ApiResponseDTO> scheduleService(@RequestBody ScheduleServiceDTO payload){
        scheduleService.scheduleService(payload, null);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Serviços agendados com sucesso"));
    }

    @PutMapping(value = "/schedule/service/{id}")
    public ResponseEntity<ApiResponseDTO> rescheduleService(
            @RequestBody ScheduleServiceDTO payload, @PathVariable(required = false) Integer id) {
        scheduleService.scheduleService(payload, id);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Serviços agendados com sucesso"));
    }

    @DeleteMapping(value = "/schedule/service/{id}")
    public ResponseEntity<ApiResponseDTO> cancelService(@PathVariable Integer id) {
        scheduleService.cancelSchedule(id);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Agendamento cancelado com sucesso"));
    }

    @RateLimitProtection
    @GetMapping(value = "/available-hours/{id}")
    public ResponseEntity<BarberAvailableTimesDTO> findAvailableHours(@PathVariable Integer id) {
        return ResponseEntity.ok(availableScheduleSlotsService.findAvailableSlots(id));
    }
}