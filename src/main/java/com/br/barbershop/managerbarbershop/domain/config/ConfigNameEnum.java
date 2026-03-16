package com.br.barbershop.managerbarbershop.domain.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigNameEnum {

    CLOSE_DAY("CLOSE-DAY"),

    /**
     * IP address allowed to perform the one-time ADMIN bootstrap registration.
     * Must be inserted directly in TB_CONFIG (BARBER_ID = NULL, LOCATION_ID = NULL)
     * before the first call to POST /v1/barber/management/barbers.
     */
    SERVER_PRINCIPLE_IP("SERVER-PRINCIPLE-IP");

    private String configName;

}
