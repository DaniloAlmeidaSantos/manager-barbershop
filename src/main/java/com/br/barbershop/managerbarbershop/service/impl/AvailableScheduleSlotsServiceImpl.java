package com.br.barbershop.managerbarbershop.service.impl;

import com.br.barbershop.managerbarbershop.domain.barber.BarberAvailableTimesDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.AvailableTimesDTO;
import com.br.barbershop.managerbarbershop.domain.schedule.ScheduleDTO;
import com.br.barbershop.managerbarbershop.repository.BarbershopConfigRepository;
import com.br.barbershop.managerbarbershop.repository.ScheduleRepository;
import com.br.barbershop.managerbarbershop.service.AvailableScheduleSlotsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AvailableScheduleSlotsServiceImpl implements AvailableScheduleSlotsService {

    private static final int APPOINTMENT_DAYS = 7;
    private static final String CONFIG_NAME = "CLOSE-DAY";

    private final ScheduleRepository scheduleRepository;

    private final BarbershopConfigRepository configRepository;

    private AvailableScheduleSlotsServiceImpl(ScheduleRepository scheduleRepository,
                                              BarbershopConfigRepository configRepository) {
        this.scheduleRepository = scheduleRepository;
        this.configRepository = configRepository;
    }

    @Override
    public BarberAvailableTimesDTO findAvailableSlots(long barber) {
        Map<LocalDate, List<LocalTime>> availableSlots = new HashMap<>();
        Map<LocalDate, List<LocalTime>> occupiedSlots = new HashMap<>();

        LocalDate currentDate = LocalDate.now();

        // Define the working hours for the barbershop
        LocalTime openingTime = LocalTime.of(9, 0); // TODO: Get the working hours in barber management table
        LocalTime closingTime = LocalTime.of(22, 0); // TODO: Get the working hours in barber management table

        log.info("Getting available appointment times for the barber: {} ", barber);

        // TODO: Getting this parameter in barber management table
        List<ScheduleDTO> occupiedAppointments = scheduleRepository.findOccupiedSchedules(APPOINTMENT_DAYS);

        // TODO: Introduce this to cache in REDIS or in machine
        Optional<String> configCloseDay = configRepository.findConfigValueToBarber(barber, CONFIG_NAME);

        List<String> closeDays = configCloseDay.isPresent() ? Arrays.asList(configCloseDay.get().split(";")) :
                new ArrayList<>();

        for (ScheduleDTO schedule : occupiedAppointments) {
            occupiedSlots
                    .computeIfAbsent(schedule.getScheduleDate(), k -> new ArrayList<>())
                    .add(schedule.getStartTime().truncatedTo(ChronoUnit.HOURS));
        }

        for (int i = 0; i < APPOINTMENT_DAYS; i++) {
            try {
                LocalDate date = currentDate.plusDays(i);
                if (closeDays.size() > 0) {
                    if (isCloseDay(date.getDayOfWeek().getValue(), closeDays)) continue;
                }

                List<LocalTime> dailySlots = new ArrayList<>();
                LocalTime currentTime = openingTime.isAfter(LocalTime.now()) ? LocalTime.now() : openingTime;

                while (currentTime.isBefore(closingTime)) {
                    boolean containsTimeInSlot = occupiedSlots.getOrDefault(date, List.of()).contains(currentTime);
                    if (!containsTimeInSlot) {
                        dailySlots.add(currentTime);
                    }

                    // TODO: Increment hours according to service *
                    currentTime = currentTime.plusHours(1);
                }

                availableSlots.put(date, dailySlots);
            } catch (Exception ex) {
                log.error("Error in loop to get available slots: {} ", ex.getMessage(), ex);
            }
        }

        var availableSlotsList = convertAvailableSlots(availableSlots);
        return new BarberAvailableTimesDTO(availableSlotsList);
    }

    private boolean isCloseDay(int day, List<String> closeDays) {
        for (String closeDay : closeDays) {
            if (Integer.valueOf(closeDay) == day) return true;
        }

        return false;
    }

    private List<AvailableTimesDTO> convertAvailableSlots(Map<LocalDate, List<LocalTime>> availableSlots) {
        List<AvailableTimesDTO> availableTimes = new ArrayList<>();

        availableSlots.forEach(
                (key, data) -> {
                    var slotDayList =  data.stream()
                            .map((time) -> new AvailableTimesDTO(key.toString(), time.toString()))
                            .collect(Collectors.toList());
                    availableTimes.addAll(slotDayList);
                }
        );

        return availableTimes;
    }
}
