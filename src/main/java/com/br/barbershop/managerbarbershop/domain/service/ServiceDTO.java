package com.br.barbershop.managerbarbershop.domain.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Integer serviceId;
    private String serviceName;
    private Double serviceValue;
}
