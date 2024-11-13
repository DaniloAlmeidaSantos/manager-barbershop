package com.br.barbershop.managerbarbershop.domain.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServicesRequestDTO {
    private String barberName;
    private String serviceName;
    private String locationName;

    // Page filters
    private int page;
    private int size;
}
