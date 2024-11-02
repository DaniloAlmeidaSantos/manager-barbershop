package com.br.barbershop.managerbarbershop.domain.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServicesDTO {
    private String barberName;
    private String serviceName;
    private String locationName;
}
