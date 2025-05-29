package com.burbujas.gestionlimpia.utils;

import jakarta.mail.internet.InternetAddress;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern REGEX_CLAVE = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");

    public static boolean validarClave(String clave) {
        if (clave == null || clave.isEmpty()) {
            return false;
        }
        if (!REGEX_CLAVE.matcher(clave).matches()) {
            return false;
        }
        return true;
    }

    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            // Validación adicional del dominio
            String dominio = email.substring(email.indexOf('@') + 1);
            if (!dominio.contains(".") || dominio.endsWith(".")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
