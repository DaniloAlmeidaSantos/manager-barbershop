package com.br.barbershop.managerbarbershop.domain.barber;

public interface BarberServicesProjection {
    Integer getBarberId();
    String getBarberName();
    String getBarberLocation();
    String getBarberCompany();
    String getBarberLocationNumber();

    // TODO: Add Longitude and Latitude to source exactly location
}