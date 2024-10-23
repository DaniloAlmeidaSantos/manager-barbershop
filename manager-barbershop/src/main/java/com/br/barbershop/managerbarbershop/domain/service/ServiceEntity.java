package com.br.barbershop.managerbarbershop.domain.service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_SERVICES")
public class ServiceEntity {
    @Id
    @Column(name = "SERVICE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "SERVICE_TYPE_NAME")
    private String serviceName;
}
