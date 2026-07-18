package com.proyectoProducto.util;

public class ValidarEmail {
    public static String limpiarEmail(String email){
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return email.replaceAll(" ","");
    }

    public static String validarYFormatearEmail(String email){
        email=limpiarEmail(email);
        if(email.matches("[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}")) {
            return email.toLowerCase();
        }
        throw new IllegalArgumentException("Email invalido");
    }
}
