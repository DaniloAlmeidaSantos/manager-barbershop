package com.br.barbershop.managerbarbershop.domain.schedule;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ScheduleStatusEnum {
    A("ACTIVE"),
    P("PENDENT"),
    C("CANCELLED"),
    F("FINISHED");

    private String description;
}
