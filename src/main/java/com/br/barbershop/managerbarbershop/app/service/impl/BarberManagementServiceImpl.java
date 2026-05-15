package com.br.barbershop.managerbarbershop.app.service.impl;

import com.br.barbershop.managerbarbershop.app.service.BarberManagementService;
import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.barber.BarberRoleEnum;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberLocationRequestDTO;
import com.br.barbershop.managerbarbershop.domain.barber.CreateBarberRequestDTO;
import com.br.barbershop.managerbarbershop.domain.config.ConfigNameEnum;
import com.br.barbershop.managerbarbershop.domain.schedule.BarberAppointmentDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleEntity;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleStatusEnum;
import com.br.barbershop.managerbarbershop.domain.user.SystemUserEntity;
import com.br.barbershop.managerbarbershop.infra.exceptions.UnauthorizedBarberOperationException;
import com.br.barbershop.managerbarbershop.infra.exceptions.UsernameAlreadyExistsException;
import com.br.barbershop.managerbarbershop.infra.repository.BarberLocationRepository;
import com.br.barbershop.managerbarbershop.infra.repository.BarberRepository;
import com.br.barbershop.managerbarbershop.infra.repository.BarbershopConfigRepository;
import com.br.barbershop.managerbarbershop.infra.repository.ScheduleRepository;
import com.br.barbershop.managerbarbershop.infra.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarberManagementServiceImpl implements BarberManagementService {

    private static final List<ScheduleStatusEnum> ACTIVE_STATUSES =
            List.of(ScheduleStatusEnum.A, ScheduleStatusEnum.P);

    private static final List<ScheduleStatusEnum> HISTORY_STATUSES =
            List.of(ScheduleStatusEnum.C, ScheduleStatusEnum.F);

    private final BarberRepository barberRepository;

    private final BarberLocationRepository barberLocationRepository;

    private final BarbershopConfigRepository barbershopConfigRepository;

    private final ScheduleRepository scheduleRepository;

    private final SystemUserRepository systemUserRepository;

    private final PasswordEncoder passwordEncoder;

    public BarberManagementServiceImpl(BarberRepository barberRepository,
                                       BarberLocationRepository barberLocationRepository,
                                       BarbershopConfigRepository barbershopConfigRepository,
                                       ScheduleRepository scheduleRepository,
                                       SystemUserRepository systemUserRepository,
                                       PasswordEncoder passwordEncoder) {
        this.barberRepository = barberRepository;
        this.barberLocationRepository = barberLocationRepository;
        this.barbershopConfigRepository = barbershopConfigRepository;
        this.scheduleRepository = scheduleRepository;
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Bootstrap operation: creates the first ADMIN barber.
     * Caller's IP must match SERVER_PRINCIPLE_IP in TB_CONFIG.
     * Only succeeds when TB_BARBERS is empty.
     */
    @Override
    @Transactional
    public void bootstrapAdminBarber(CreateBarberRequestDTO request, String requestIp) {
        if (barberRepository.count() > 0) {
            log.warn("Bootstrap rejected: barbers already exist in the database.");
            throw new UnauthorizedBarberOperationException();
        }

        validateBootstrapIp(requestIp);
        saveBarber(request, BarberRoleEnum.ADMIN);
        log.info("Bootstrap complete: ADMIN barber '{}' created.", request.name());
    }

    /**
     * Registers a new EMPLOYEE barber.
     * Role-based access is enforced upstream by Spring Security (BARBER_ADMIN only).
     */
    @Override
    @Transactional
    public void registerEmployeeBarber(CreateBarberRequestDTO request) {
        saveBarber(request, BarberRoleEnum.EMPLOYEE);
        log.info("EMPLOYEE barber '{}' registered.", request.name());
    }

    /**
     * Creates a new barbershop location.
     * Role-based access is enforced upstream by Spring Security (BARBER_ADMIN only).
     */
    @Override
    @Transactional
    public void createLocation(CreateBarberLocationRequestDTO request) {
        barberLocationRepository.save(request.toEntity());
        log.info("Location '{}' created.", request.locationName());
    }

    @Override
    public List<BarberAppointmentDTO> getCurrentAppointments(Integer barberId) {
        log.info("Fetching current appointments for barber {}", barberId);
        return scheduleRepository.findByBarberIdAndStatuses(barberId, ACTIVE_STATUSES)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BarberAppointmentDTO> getAppointmentHistory(Integer barberId) {
        log.info("Fetching appointment history for barber {}", barberId);
        return scheduleRepository.findByBarberIdAndStatuses(barberId, HISTORY_STATUSES)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a SystemUserEntity (hashed password) and a BarberEntity linked to it
     * in a single transactional unit. The system role stored in TB_SYSTEM_USERS mirrors
     * the barber role (BARBER_ADMIN or BARBER_EMPLOYEE) so that DaoAuthenticationProvider
     * loads the correct Spring Security authority (ROLE_BARBER_ADMIN / ROLE_BARBER_EMPLOYEE).
     */
    private void saveBarber(CreateBarberRequestDTO request, BarberRoleEnum role) {
        if (systemUserRepository.findByUsername(request.username()) != null) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        String systemRole = role == BarberRoleEnum.ADMIN ? "BARBER_ADMIN" : "BARBER_EMPLOYEE";

        SystemUserEntity systemUser = SystemUserEntity.builder()
                .role(systemRole)
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        SystemUserEntity savedSystemUser = systemUserRepository.save(systemUser);

        BarberEntity newBarber = BarberEntity.builder()
                .name(request.name())
                .locationId(barberLocationRepository.getReferenceById(request.locationId()))
                .role(role)
                .systemUser(savedSystemUser)
                .build();

        barberRepository.save(newBarber);
        log.info("Barber '{}' created with role {} and system user '{}'.",
                request.name(), role, request.username());
    }

    /**
     * Validates the caller IP against SERVER_PRINCIPLE_IP in TB_CONFIG.
     * Returns the same 403 for both "config absent" and "IP mismatch"
     * to avoid leaking setup details.
     */
    private void validateBootstrapIp(String requestIp) {
        Optional<String> allowedIp = barbershopConfigRepository
                .findGlobalConfigValue(ConfigNameEnum.SERVER_PRINCIPLE_IP.getConfigName());

        if (allowedIp.isEmpty()) {
            log.error("Bootstrap blocked: SERVER_PRINCIPLE_IP is not configured in TB_CONFIG.");
            throw new UnauthorizedBarberOperationException();
        }

        if (!allowedIp.get().equals(requestIp)) {
            log.warn("Bootstrap blocked: request from IP '{}' does not match allowed IP '{}'.",
                    requestIp, allowedIp.get());
            throw new UnauthorizedBarberOperationException();
        }

        log.info("Bootstrap IP validation passed for '{}'.", requestIp);
    }

    private BarberAppointmentDTO toDTO(ScheduleEntity entity) {
        return new BarberAppointmentDTO(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getCustomer().getName(),
                entity.getCustomer().getPhone(),
                entity.getScheduleServices(),
                entity.getStartTime(),
                entity.getFinishTime(),
                entity.getStatusEnum().name()
        );
    }
}
