package com.br.barbershop.managerbarbershop.app.utils;

import com.br.barbershop.managerbarbershop.infra.exceptions.EmailValidatorException;
import java.util.regex.Pattern;

public class EmailValidatorUtils {

    public static void isEmailValid(String email) {
        final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        final Pattern pattern = Pattern.compile(emailRegex);
        final boolean isValid = pattern.matcher(email).matches();

        if (!isValid) {
            throw new EmailValidatorException(email);
        }
    }

}
