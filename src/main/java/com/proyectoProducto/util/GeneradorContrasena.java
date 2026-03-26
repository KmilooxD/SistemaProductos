package com.proyectoProducto.util;
import java.security.SecureRandom;

public class GeneradorContrasena {
    private static final String CARACTERES="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generarContrasena(int longitud) {
        if (longitud <= 0) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        StringBuilder contrasena = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            int indice = secureRandom.nextInt(CARACTERES.length());
            contrasena.append(CARACTERES.charAt(indice));

        }
        return contrasena.toString();
    }
}

