package com.br.barbershop.managerbarbershop.domain.schedule;

import com.br.barbershop.managerbarbershop.domain.barber.BarberEntity;
import com.br.barbershop.managerbarbershop.domain.customer.CustomerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_SCHEDULE")
public class ScheduleEntity {
    @Id
    @Column(name = "SCHEDULE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BARBER_ID", referencedColumnName = "BARBER_ID")
    private BarberEntity barber;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID")
    private CustomerEntity customer;

    @Column(name = "SCHEDULE_START_TIME")
    private LocalDateTime startTime;

    @Column(name = "SCHEDULE_FINISH_TIME")
    private LocalDateTime finishTime;

    @Column(name = "SCHEDULE_STATUS")
    private ScheduleStatusEnum statusEnum;

    @Column(name = "SCHEDULE_SERVICES")
    private String scheduleServices;
}
