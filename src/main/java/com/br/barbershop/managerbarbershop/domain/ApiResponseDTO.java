package com.br.barbershop.managerbarbershop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDTO {
    private String status;
    private String message;
}
