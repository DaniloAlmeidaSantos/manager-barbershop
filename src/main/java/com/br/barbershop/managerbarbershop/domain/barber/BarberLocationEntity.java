package com.br.barbershop.managerbarbershop.domain.barber;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_BARBER_LOCATION")
public class BarberLocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCATION_ID")
    private Integer id;

    @Column(name = "LOCATION_COMPANY_NAME")
    private String locationCompanyName;

    @Column(name = "LOCATION_NAME")
    private String locationName;

    @Column(name = "LOCATION_POSTAL_CODE")
    private String postalCode;

    @Column(name = "LOCATION_COMPLEMENT")
    private String complement;

    @Column(name = "LOCATION_NUMBER")
    private String number;

    @Column(name = "LOCATION_STATE")
    private char state;

    @Column(name = "LOCATION_CITY")
    private String city;
}
