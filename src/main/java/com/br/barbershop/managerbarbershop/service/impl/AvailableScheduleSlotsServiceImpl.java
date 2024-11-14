package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.barber.BarberAvailableTimesDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleDTO;
import com.br.barbershop.managerbarbershop.repository.ScheduleRepository;
import com.br.barbershop.managerbarbershop.service.AvailableScheduleSlotsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AvailableScheduleSlotsServiceImpl implements AvailableScheduleSlotsService {

    private static final int APPOINTMENT_DAYS = 7;

    private final ScheduleRepository scheduleRepository;

    private AvailableScheduleSlotsServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public BarberAvailableTimesDTO getAvailableSlots(long barber) {
        Map<LocalDate, List<LocalTime>> availableSlots = new HashMap<>();
        Map<LocalDate, List<LocalTime>> occupiedSlots = new HashMap<>();

        LocalDate currentDate = LocalDate.now();

        // Define the working hours for the barbershop
        LocalTime openingTime = LocalTime.of(9, 0); // TODO: Get the working hours in barber management table
        LocalTime closingTime = LocalTime.of(22, 0); // TODO: Get the working hours in barber management table

        log.info("Getting available appointment times for the barber: {} ", barber);

        // TODO: Getting this parameter in barber management table
        List<ScheduleDTO> occupiedAppointments = scheduleRepository.findOccupiedSchedules(APPOINTMENT_DAYS);

        for (ScheduleDTO schedule : occupiedAppointments) {
            occupiedSlots
                    .computeIfAbsent(schedule.getScheduleDate(), k -> new ArrayList<>())
                    .add(schedule.getStartTime().truncatedTo(ChronoUnit.HOURS));
        }

        for (int i = 0; i < APPOINTMENT_DAYS; i++) {
            try {
                LocalDate date = currentDate.plusDays(i);

                // TODO: Create method for this
                // TODO: Getting settings in database for close schedule in determined dates
                // TODO: In the database, save the days when the barbershop is closed in this format (1;2;3;4;5;6;7) and use DayOfWeek.of(1) for validation.
                // TODO: Remind to use loop for getting data in database for compare (date.getDayOfWeek().getValue() == databaseValue)
                if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    continue;
                }

                List<LocalTime> dailySlots = new ArrayList<>();
                LocalTime currentTime = openingTime.isAfter(LocalTime.now()) ? LocalTime.now() : openingTime;

                while (currentTime.isBefore(closingTime)) {
                    if (!occupiedSlots.getOrDefault(date, List.of()).contains(currentTime)) {
                        dailySlots.add(currentTime);
                    }

                    // TODO: Increment hours according to service *
                    currentTime = currentTime.plusHours(1);
                }

                availableSlots.put(date, dailySlots);
            } catch (Exception ex) {
                log.error("Error in loop get available slots: {} ", ex.getMessage(), ex);
            }
        }

        return new BarberAvailableTimesDTO(availableSlots);
    }
}
