package com.br.barbershop.managerbarbershop.exceptions;

import lombok.Getter;

public class ScheduleConflictsException extends RuntimeException {

    @Getter private boolean mismatchDateTime;

    public ScheduleConflictsException(String startTime) {
        super("This date and time have been previously scheduled " + startTime);
        this.mismatchDateTime = false;
    }

    public ScheduleConflictsException(String startTime, String finishTime) {
        super("Your start date and time " + startTime + " cannot be after your end time " + finishTime);
        this.mismatchDateTime = true;
    }

}
