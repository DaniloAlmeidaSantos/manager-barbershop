package com.br.barbershop.managerbarbershop.domain.barber;

import com.br.barbershop.managerbarbershop.domain.user.SystemUserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_BARBERS")
public class BarberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BARBER_ID")
    private Integer id;

    @Column(name = "BARBER_NAME")
    private String name;

    @OneToOne
    @JoinColumn(name = "BARBER_LOCATION_ID", referencedColumnName = "LOCATION_ID")
    private BarberLocationEntity locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "BARBER_ROLE", nullable = false)
    private BarberRoleEnum role;

    @OneToOne
    @JoinColumn(name = "SYS_USER_ID", referencedColumnName = "SYS_ID")
    private SystemUserEntity systemUser;
}
