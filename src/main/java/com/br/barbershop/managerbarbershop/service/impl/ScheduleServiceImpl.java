package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerEntity;
import com.br.barbershop.managerbarbershop.domain.schedule.RescheduleAppointmentDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleEntity;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleServiceDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleStatusEnum;
import com.br.barbershop.managerbarbershop.domain.service.ServicesCostEntity;
import com.br.barbershop.managerbarbershop.exceptions.ScheduleConflictsException;
import com.br.barbershop.managerbarbershop.exceptions.ScheduleServicesException;
import com.br.barbershop.managerbarbershop.exceptions.ServiceException;
import com.br.barbershop.managerbarbershop.repository.BarberRepository;
import com.br.barbershop.managerbarbershop.repository.CustomerRepository;
import com.br.barbershop.managerbarbershop.repository.ScheduleRepository;
import com.br.barbershop.managerbarbershop.repository.ServicesCostRepository;
import com.br.barbershop.managerbarbershop.service.ScheduleService;
import com.br.barbershop.managerbarbershop.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final BarberRepository barberRepository;

    private final ServicesCostRepository servicesCostRepository;

    private final ScheduleRepository scheduleRepository;

    private final CustomerRepository customerRepository;

    public ScheduleServiceImpl(BarberRepository barberRepository,
                               ServicesCostRepository servicesCostRepository,
                               ScheduleRepository scheduleRepository,
                               CustomerRepository customerRepository) {
        this.barberRepository = barberRepository;
        this.servicesCostRepository = servicesCostRepository;
        this.scheduleRepository = scheduleRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    @Transactional(rollbackOn = ScheduleServicesException.class)
    public void scheduleService(ScheduleServiceDTO payload, @Nullable Integer appointmentId) {
        // TODO: Validate if start time to schedule is after at 09am
        LocalDateTime startDateTime = DateUtils.convertStringToDate(payload.startTime());

        // TODO: Validate if finish time to schedule is before at 10pm
        LocalDateTime finishDateTime = DateUtils.convertStringToDate(payload.finishTime());

        if (startDateTime.isEqual(finishDateTime) || startDateTime.isAfter(finishDateTime)) {
            log.info("Your start date and time ({}) cannot be after your end time ({}). ",
                    startDateTime, finishDateTime);
            throw new ScheduleConflictsException(startDateTime.toString(), finishDateTime.toString());
        }

        BarberEntity barber =  BarberEntity.builder().id(payload.barber()).build();

        try {

            // TODO: Introduce in Redis this search's
            CustomerEntity customer = customerRepository.getReferenceById(payload.customer());
            List<ServicesCostEntity> services = servicesCostRepository.findByBarberId(barber);

            // Incluir validação impossibilitando de um mesmo usuário agendar mais de um horário no salão no mesmo dia? *
            if (services != null ) {

                validateExistenceOfServices(payload.services(), services);
                validatePreviousDatesScheduled(barber.getId(), startDateTime, finishDateTime);

                scheduleRepository.saveAndFlush(
                        convertToEntity(barber, startDateTime, finishDateTime, customer, payload.services(), appointmentId)
                );
            }

            log.info("Scheduling service {} to user {}", payload.services().toArray(), payload.customer());
        } catch (Exception ex) {
            log.error("Error to schedule services to {} for services {}: {}", payload.customer(), payload.services(), ex.getMessage(), ex);
            throw new ScheduleServicesException("Error to schedule service to user: " + payload.customer(), ex);
        }

    }

    private void validateExistenceOfServices(Set<Integer> payloadServicesIds, List<ServicesCostEntity> services) {
        String barberName = services.get(0).getBarberId().getName();

        Set<Integer> servicesIds = services.stream()
                .map(service -> service.getServiceId().getId())
                .collect(Collectors.toSet());

        Optional<Integer> divergentService = payloadServicesIds.stream()
                .filter(id -> !servicesIds.contains(id))
                .findFirst();

        if (divergentService.isPresent()) {
            log.error("Unavailable services to barber {}: {} ", barberName, services.toArray());
            throw new ServiceException(divergentService.get(), barberName);
        }

        log.info("Available services to barber {}: {} ", barberName, services.toArray());
    }

    private void validatePreviousDatesScheduled(Integer barberId, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        var barberSchedules =
                scheduleRepository.findByBarberIdAndStartDate(barberId, Date.valueOf(startDateTime.toLocalDate()));

        var scheduleConflict = barberSchedules.stream()
                .filter(schedule -> !(
                        finishDateTime.isBefore(schedule.getStartTime()) || startDateTime.isAfter(schedule.getFinishTime())
                ))
                .findFirst();

        if (scheduleConflict.isPresent()) {
            log.info("Unavailable dates to schedule service, because this date and time has previous scheduled.");
            throw new ScheduleConflictsException(startDateTime.toString());
        }

        log.info("Available dates to schedule service.");
    }

    private ScheduleEntity convertToEntity(BarberEntity barber,
                                           LocalDateTime startTime,
                                           LocalDateTime finishTime,
                                           CustomerEntity customer,
                                           Set<Integer> servicesIds,
                                           Integer appointmentId) {
        return ScheduleEntity.builder()
                .id(appointmentId)
                .barber(barber)
                .scheduleServices(servicesIds.toString())
                .customer(customer)
                .statusEnum(ScheduleStatusEnum.A)
                .startTime(startTime)
                .finishTime(finishTime)
                .build();
    }
}
