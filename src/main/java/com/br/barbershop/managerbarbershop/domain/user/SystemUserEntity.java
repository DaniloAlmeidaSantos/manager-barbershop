package com.br.barbershop.managerbarbershop.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "TB_SYSTEM_USERS")
@AllArgsConstructor
@NoArgsConstructor
public class SystemUserEntity {
    @Id
    @Column(name = "SYS_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "SYS_ROLE")
    private String role;

    @Column(name = "SYS_USERNAME", unique = true, nullable = false)
    private String username;

    @Column(name = "SYS_PASSWORD", nullable = false)
    private String password;
}
