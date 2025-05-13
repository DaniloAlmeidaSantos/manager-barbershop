package com.br.barbershop.managerbarbershop.app.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {

    public static LocalDateTime convertStringToDate(String value) {

        if (value == null) {
            throw new NullPointerException("Date and time informed is null.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(value, formatter);
    }
}
