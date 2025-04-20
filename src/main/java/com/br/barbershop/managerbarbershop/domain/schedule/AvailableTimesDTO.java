package com.br.barbershop.managerbarbershop.domain.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimesDTO {
    private String date;
    private String time;
}
