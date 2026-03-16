package com.br.barbershop.managerbarbershop.app.service;

import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberLocationRequestDTO;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberRequestDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.BarberAppointmentDTO;

import java.util.List;

/**
 * Provides barber-specific appointment management and barber registration operations.
 * Segregated from BarberService (query) and ScheduleService (customer scheduling) — ISP.
 */
public interface BarberManagementService {

    /**
     * One-time bootstrap operation: creates the first ADMIN barber.
     * Only allowed when TB_BARBERS is empty.
     * The caller's IP must match SERVER_PRINCIPLE_IP in TB_CONFIG.
     *
     * @param request   name, locationId, username and password of the new barber
     * @param requestIp client IP extracted from the HTTP request
     */
    void bootstrapAdminBarber(CreateBarberRequestDTO request, String requestIp);

    /**
     * Registers a new EMPLOYEE barber.
     * Caller must be an authenticated ADMIN barber (enforced via Spring Security in SecurityConfig).
     *
     * @param request name, locationId, username and password of the new barber
     */
    void registerEmployeeBarber(CreateBarberRequestDTO request);

    /**
     * Creates a new barbershop location.
     * Caller must be an authenticated ADMIN barber (enforced via Spring Security in SecurityConfig).
     */
    void createLocation(CreateBarberLocationRequestDTO request);

    /**
     * Returns all active and pending appointments for the given barber.
     */
    List<BarberAppointmentDTO> getCurrentAppointments(Integer barberId);

    /**
     * Returns the full history (cancelled and finished) for the given barber.
     */
    List<BarberAppointmentDTO> getAppointmentHistory(Integer barberId);
}
