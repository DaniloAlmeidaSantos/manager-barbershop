package com.br.barbershop.managerbarbershop.domain.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")
    private int id;

    @Column(name = "CUSTOMER_NAME")
    private String name;

    @Column(name = "CUSTOMER_PHONE")
    private String phone;

    @Column(name = "CUSTOMER_PASSWORD")
    private String password;

    @Column(name = "CUSTOMER_EMAIL")
    private String email;

    public CustomerDTO convertToDTO() {
        return new CustomerDTO(this.id, this.name, this.phone, this.password, this.email);
    }
}
