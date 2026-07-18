package com.proyectoProducto.util;

public class FormatearTexto {
    public static String formatearNombre(String nombre){
        nombre = nombre.trim().replaceAll("\\s+", " ");
        return nombre;
    }
}
