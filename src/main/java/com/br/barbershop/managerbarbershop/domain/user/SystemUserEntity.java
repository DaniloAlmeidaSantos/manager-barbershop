package com.br.barbershop.managerbarbershop.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_system_users")
@AllArgsConstructor
@NoArgsConstructor
public class SystemUserEntity {
    @Id
    @Column(name = "SYS_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "SYS_ROLE")
    private String role;

    @Column(name = "SYS_USERNAME")
    private String username;

    @Column(name = "SYS_PASSWORD")
    private String password;


}
