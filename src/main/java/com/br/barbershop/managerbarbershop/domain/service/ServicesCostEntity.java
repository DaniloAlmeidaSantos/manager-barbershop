package com.br.barbershop.managerbarbershop.domain.service;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_VALUES_SHEET")
public class ServicesCostEntity {
    @Id
    @Column(name = "SHEET_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "BARBER_ID", referencedColumnName = "BARBER_ID")
    private BarberEntity barberId;

    @ManyToOne
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "SERVICE_ID")
    private ServiceEntity serviceId;

    @Column(name = "SHEET_SERVICE_VALUE")
    private double value;
}
