package com.br.barbershop.managerbarbershop.domain.barber;

import com.br.barbershop.managerbarbershop.domain.service.ServiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarberServicesDTO {
    private Integer barberId;
    private String barberName;
    private String barberLocation;
    private List<ServiceDTO> services;
}
