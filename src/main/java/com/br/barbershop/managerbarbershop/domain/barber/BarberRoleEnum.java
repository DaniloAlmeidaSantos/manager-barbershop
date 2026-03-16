package com.br.barbershop.managerbarbershop.domain.barber;

/**
 * Defines the access level of a barber within the system.
 *
 * ADMIN  — Can register new employee barbers and perform all management operations.
 * EMPLOYEE — Can manage appointments but cannot register new barbers.
 *
 * The very first barber registered when the table is empty is automatically
 * promoted to ADMIN (bootstrap rule). All subsequent registrations default to EMPLOYEE
 * and must be authorized by an existing ADMIN.
 */
public enum BarberRoleEnum {
    ADMIN,
    EMPLOYEE
}
