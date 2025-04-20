package com.br.barbershop.managerbarbershop.domain.config;

import com.br.barbershop.managerbarbershop.domain.barber.BarberLocationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CONFIG")
public class BarbershopConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID")
    private Integer id;

    @Column(name = "CONFIG_NAME")
    private String name;

    @Column(name = "CONFIG_VALUE")
    private String value;

    @OneToOne
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID")
    private BarberLocationEntity locationId;
}
