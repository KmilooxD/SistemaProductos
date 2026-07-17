package com.proyectoProducto.util;

public class ValidarTelefono {
    public static String limpiarTelefono(String telefono){
        if(telefono==null){
            throw new IllegalArgumentException("El telefono es obligatorio");
        }
        return telefono.replaceAll(" ", "");
    }

    public static String validarYFormatearTelefono(String telefono){
        telefono=limpiarTelefono(telefono);
       if(telefono.matches("9\\d{8}")) {
           telefono = "+56" + telefono;
       }
       if(telefono.matches("\\+56\\d{9}")) {
           return telefono;
       }
       throw new IllegalArgumentException("Telefono invalido");
    }
}
