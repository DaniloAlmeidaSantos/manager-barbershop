package com.br.barbershop.managerbarbershop.app.controller;

import com.br.barbershop.managerbarbershop.app.service.BarberManagementService;
import com.br.barbershop.managerbarbershop.app.service.ScheduleService;
import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberLocationRequestDTO;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberRequestDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.BarberAppointmentDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints reserved for barbers to manage appointments and registrations.
 *
 * Role rules (enforced by SecurityConfig):
 *  - BARBER_ADMIN can register new EMPLOYEE barbers and create locations.
 *  - Both BARBER_ADMIN and BARBER_EMPLOYEE can manage appointments.
 *
 * The barber identity is resolved from the JWT via authentication.getDetails().
 * No requestingBarberId parameter is needed — the token already carries the caller's id.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(value = "/v1/barber/management")
public class BarberManagementController {

    private final BarberManagementService barberManagementService;

    private final ScheduleService scheduleService;

    // -------------------------------------------------------------------------
    // Barber registration (BARBER_ADMIN only — enforced by SecurityConfig)
    // -------------------------------------------------------------------------

    /**
     * Registers a new EMPLOYEE barber.
     * POST /v1/barber/management/barbers
     */
    @PostMapping("/barbers")
    public ResponseEntity<ApiResponseDTO> registerEmployeeBarber(@RequestBody @Valid CreateBarberRequestDTO payload) {
        barberManagementService.registerEmployeeBarber(payload);
        return new ResponseEntity<>(
                new ApiResponseDTO(String.valueOf(HttpStatus.CREATED.value()), "Barbeiro cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    // -------------------------------------------------------------------------
    // Location management (BARBER_ADMIN only — enforced by SecurityConfig)
    // -------------------------------------------------------------------------

    /**
     * Creates a new barbershop location.
     * POST /v1/barber/management/locations
     */
    @PostMapping("/locations")
    public ResponseEntity<ApiResponseDTO> createLocation(
            @RequestBody @Valid CreateBarberLocationRequestDTO payload) {

        barberManagementService.createLocation(payload);
        return new ResponseEntity<>(
                new ApiResponseDTO(String.valueOf(HttpStatus.CREATED.value()), "Localização criada com sucesso"),
                HttpStatus.CREATED
        );
    }

    // -------------------------------------------------------------------------
    // Appointment management
    // -------------------------------------------------------------------------

    /**
     * Lists all active and pending appointments for the authenticated barber.
     * GET /v1/barber/management/appointments
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<BarberAppointmentDTO>> getCurrentAppointments(Authentication authentication) {
        Integer barberId = (Integer) authentication.getDetails();
        return ResponseEntity.ok(barberManagementService.getCurrentAppointments(barberId));
    }

    /**
     * Lists the full appointment history (cancelled + finished) for the authenticated barber.
     * GET /v1/barber/management/appointments/history
     */
    @GetMapping("/appointments/history")
    public ResponseEntity<List<BarberAppointmentDTO>> getAppointmentHistory(Authentication authentication) {
        Integer barberId = (Integer) authentication.getDetails();
        return ResponseEntity.ok(barberManagementService.getAppointmentHistory(barberId));
    }

    /**
     * Schedules an appointment for any customer.
     * POST /v1/barber/management/appointments
     */
    @PostMapping("/appointments")
    public ResponseEntity<ApiResponseDTO> scheduleForCustomer(@RequestBody ScheduleServiceDTO payload) {
        scheduleService.scheduleService(payload, null);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Serviço agendado com sucesso"));
    }

    /**
     * Reschedules an existing appointment by ID.
     * PUT /v1/barber/management/appointments/{id}
     */
    @PutMapping("/appointments/{id}")
    public ResponseEntity<ApiResponseDTO> rescheduleAppointment(
            @RequestBody ScheduleServiceDTO payload, @PathVariable Integer id) {
        scheduleService.scheduleService(payload, id);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Serviço reagendado com sucesso"));
    }

    /**
     * Cancels any appointment by ID.
     * DELETE /v1/barber/management/appointments/{id}
     */
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<ApiResponseDTO> cancelAppointment(@PathVariable Integer id) {
        scheduleService.cancelSchedule(id);
        return ResponseEntity.ok(new ApiResponseDTO("200", "Agendamento cancelado com sucesso"));
    }
}
