package com.proyectoProducto.util;

import com.proyectoProducto.model.Usuario;

public class ValidarUsuario {
    public static void validarUsuarioActivo(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        if(!usuario.getActivo()){
            throw new RuntimeException("Usuario inactivo");
        }
    }
    public static void validarAdmin(Usuario admin){
        validarUsuarioActivo(admin);
        if(admin.getRol() != Usuario.Rol.ADMIN) {
            throw new IllegalArgumentException("No tienes permisos para realizar esta operacion");
        }
    }
}
