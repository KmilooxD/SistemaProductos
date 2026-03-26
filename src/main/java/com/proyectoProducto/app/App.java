package com.proyectoProducto.app;

import com.proyectoProducto.dao.UsuarioDAO;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.service.UsuarioService;
import java.util.List;
import java.util.Scanner;


public class App {
    static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        Scanner sc = new Scanner(System.in);
        Usuario usuarioLogueado;
        try {
            String email, contrasena;

            System.out.println("****Sistema De Ventas****");
            System.out.println("INGRESA EMAIL");
            email = sc.nextLine();
            System.out.println("INGRESA CONTRASENA");
            contrasena = sc.nextLine();

            usuarioLogueado = usuarioService.validarLogin(email, contrasena);

            System.out.println("+++BIENVENIDO+++");
            System.out.println("Usuario: " + usuarioLogueado.getNombre());
            System.out.println("ROL: " + usuarioLogueado.getRol());

            validarCambioContrasena(usuarioLogueado,usuarioService,sc);
            if(usuarioLogueado.getRol()==Usuario.Rol.ADMIN){
                menuAdmin(usuarioService,usuarioLogueado,sc);
            }else{
                menuVendedor(usuarioService, usuarioLogueado, sc);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void menuAdmin(UsuarioService usuarioService, Usuario admin,Scanner sc){
        String CrearUsuariNombre,crearUsuarioEmail,actualizarUsuarioNombre,actualizarUsuarioEmail,buscarPorEmail;
        int opcionMenuAdmin,actualizarUsuarioId,actualizarActivoId,buscarPorId;

        do{
            System.out.println("**** MENU ADMIN ****");
            System.out.println("Ingresa una opcion");
            System.out.println("1. Crear usuario");
            System.out.println("2. Modificar usuario");
            System.out.println("3. Activar/desactivar usuario");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Listar usuarios activos");
            System.out.println("6. Buscar usuario por id");
            System.out.println("7. Buscar usuario por email");
            System.out.println("8. Salir");
            opcionMenuAdmin = sc.nextInt();
            sc.nextLine();
            switch(opcionMenuAdmin){
                case 1:
                    System.out.println("**** Creacion Usuario ****");
                    System.out.println("Ingresa Nombre completo");
                    CrearUsuariNombre = sc.nextLine();
                    System.out.println("Ingresa Email");
                    crearUsuarioEmail = sc.nextLine();
                    Usuario.Rol crearUsuarioRol=opcionRol(sc);

                    Usuario usuarioNuevo=new Usuario();
                    usuarioNuevo.setNombre(CrearUsuariNombre);
                    usuarioNuevo.setEmail(crearUsuarioEmail);
                    usuarioNuevo.setRol(crearUsuarioRol);

                    usuarioService.crearUsuario(admin,usuarioNuevo);
                    break;
                case 2:
                    System.out.println("**** Actualizar Usuario ****");
                    System.out.println("Ingresa el id del usuario");
                    actualizarUsuarioId = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Ingresa Nombre completo");
                    actualizarUsuarioNombre = sc.nextLine();
                    System.out.println("Ingresa Email");
                    actualizarUsuarioEmail = sc.nextLine();

                    boolean actualizarUsuarioActivo=opcionActivo(sc);
                    Usuario.Rol actualizarUsuarioRol=opcionRol(sc);

                    Usuario usuarioActualizado=new Usuario();
                    usuarioActualizado.setIdUsuario(actualizarUsuarioId);
                    usuarioActualizado.setNombre(actualizarUsuarioNombre);
                    usuarioActualizado.setEmail(actualizarUsuarioEmail);
                    usuarioActualizado.setRol(actualizarUsuarioRol);
                    usuarioActualizado.setActivo(actualizarUsuarioActivo);

                   if(usuarioService.actualizarUsuario(admin,usuarioActualizado)){
                       System.out.println("Usuario Actualizado correctamente");
                   }
                    break;
                case 3:
                    System.out.println("**** Activar/Desactivar Usuario ****");
                    System.out.println("Ingresa el id del usuario");
                    actualizarActivoId=sc.nextInt();
                    boolean actualizarActivo =opcionActivo(sc);

                    Usuario usuarioActivo=new Usuario();
                    usuarioActivo.setIdUsuario(actualizarActivoId);
                    usuarioActivo.setActivo(actualizarActivo);

                    if(usuarioService.cambiarActivo(admin,usuarioActivo)){
                        System.out.println("Usuario Actualizado correctamente");
                    }
                    break;
                case 4:
                    System.out.println("**** Usuarios ****");
                    List<Usuario>listarUsuario=usuarioService.listarUsuarios();
                    recorrerLista(listarUsuario);
                    break;
                case 5:
                    System.out.println("**** Usuarios ****");
                    List<Usuario>listarUsuarioActivos=usuarioService.listarUsuariosActivos();
                    recorrerLista(listarUsuarioActivos);
                    break;
                case 6:
                    System.out.println("**** Buscar por id ****");
                    System.out.println("Ingresa el id del usuario");
                    buscarPorId=sc.nextInt();
                    try {
                        Usuario usuarioId=usuarioService.buscarPorId(buscarPorId);
                        usuarioExistente(usuarioId);
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("**** Buscar por email ****");
                    System.out.println("Ingresa el email del usuario");
                    buscarPorEmail=sc.nextLine();
                    try {
                        Usuario usuarioEmail=usuarioService.buscarPorEmail(buscarPorEmail);
                        usuarioExistente(usuarioEmail);
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 8:
                    return;
            }
        }while(opcionMenuAdmin!=8);
    }
    private static void menuVendedor(UsuarioService usuarioService, Usuario vendedor,Scanner sc){
        String contrasenaActualVendedor, contrasenaNuevaVendedor;
        int opcionMenuVendedor;

        do{
            System.out.println("**** MENU VENDEDOR ****");
            System.out.println("Ingresa una opcion");
            System.out.println("1. Cambiar contrasena");
            System.out.println("2. Salir");
            System.out.println();
            opcionMenuVendedor = sc.nextInt();
            sc.nextLine();

            switch(opcionMenuVendedor){
                case 1:
                    System.out.println("Ingresa contrasena actual");
                    contrasenaActualVendedor=sc.nextLine();
                    System.out.println("Ingresa contrasena nueva");
                    contrasenaNuevaVendedor=sc.nextLine();

                   if(usuarioService.cambiarContrasena(vendedor,contrasenaActualVendedor,contrasenaNuevaVendedor)){
                       System.out.println("Contrasena actualizada correctamente");
                   }
                    break;
            }
        }while(opcionMenuVendedor!=2);
    }
    private static Usuario.Rol opcionRol(Scanner sc){
        System.out.println("Seleccione rol:");
        System.out.println("1. ADMIN");
        System.out.println("2. VENDEDOR");

        int opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion) {
            case 1:
                return Usuario.Rol.ADMIN;
            case 2:
                return Usuario.Rol.VENDEDOR;
            default:
                throw new IllegalArgumentException("Rol inválido");
        }
    }
    private static boolean opcionActivo(Scanner sc){
        System.out.println("Selecione Activar/Desactivar usuario");
        System.out.println("1. ACTIVO");
        System.out.println("2. DESACTIVAR");
        int opcion = sc.nextInt();
        sc.nextLine();
        switch (opcion) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                throw new IllegalArgumentException("Opcion invalida");

        }
    }
    private static void recorrerLista(List<Usuario> usuarios){
        for(Usuario usuario:usuarios){
            System.out.println("------------------------------");
            System.out.println("Id: "+usuario.getIdUsuario());
            System.out.println("Nombre: "+usuario.getNombre());
            System.out.println("Email: "+usuario.getEmail());
            System.out.println("Rol: "+usuario.getRol());
            System.out.println("Activo: "+usuario.getActivo());
            System.out.println("Fecha Creacion: "+usuario.getFechaCreacion());
            System.out.println("Ultima Sesion: "+usuario.getUltimaSesion());
        }
    }
    private static void usuarioExistente(Usuario usuario){
            System.out.println("Id: "+usuario.getIdUsuario());
            System.out.println("Nombre: "+usuario.getNombre());
            System.out.println("Email: "+usuario.getEmail());
            System.out.println("Rol: "+usuario.getRol());
            System.out.println("Activo: "+usuario.getActivo());
            System.out.println("Fecha Creacion: "+usuario.getFechaCreacion());
            System.out.println("Ultima Sesion: "+usuario.getUltimaSesion());
    }
    private static String cambiarContrasenaPrimerLogin(Scanner sc){
        String contrasenaPrimerlogin;
        System.out.println("Ingresa la nueva contrasena");
        contrasenaPrimerlogin=sc.nextLine();
        return contrasenaPrimerlogin;
    }
    private static void validarCambioContrasena(Usuario usuario,UsuarioService usuarioService,Scanner sc){
        if(usuario.getCambiarContrasena()){
            System.out.println("Cambio obligatorio de contrasena");
            usuarioService.cambiarContrasenaPrimerLogin(usuario,cambiarContrasenaPrimerLogin(sc));
        }
    }

}

