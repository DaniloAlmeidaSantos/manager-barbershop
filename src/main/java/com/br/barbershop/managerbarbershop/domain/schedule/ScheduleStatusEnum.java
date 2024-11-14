package com.br.barbershop.managerbarbershop.domain.schedule;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ScheduleStatusEnum {
    A("ACTIVE"),
    P("PENDENT"),
    C("CANCELLED"),
    F("FINISHED");

    private String description;

    public boolean isActive() {
        return this == A;
    }

    public boolean isPendent() {
        return this == P;
    }

    public boolean isCancelled() {
        return this == C;
    }

    public boolean isFinished() {
        return this == F;
    }

    public ScheduleStatusEnum getNextStatus() {
        switch (this) {
            case P: return A;
            case A: return F;
            default: return this; // If cancelled or finished not have next step
        }
    }
}
