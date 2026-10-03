package com.proyectoProducto.service;

import com.proyectoProducto.dao.UsuarioDAO;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.util.GeneradorContrasena;
import com.proyectoProducto.util.ValidarUsuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;


public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {

        this.usuarioDAO = usuarioDAO;
    }
    public List<Usuario>listarUsuarios(){
        return usuarioDAO.listarUsuarios();
    }
    public List<Usuario>listarUsuariosActivos(){
        return usuarioDAO.listarUsuariosActivos();
    }

    public Usuario buscarUsuarioPorId(int id){
        validarIdUsuairo(id);
        return usuarioDAO.buscarUsuarioPorId(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public Usuario buscarUsuarioPorEmail(String email){
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return usuarioDAO.buscarUsuarioPorEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public Usuario validarLoginUsuario(String email, String contrasena) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        Usuario usuario = usuarioDAO.buscarUsuarioPorEmailLogin(email).orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
        ValidarUsuario.validarUsuarioActivo(usuario);

        if (!validarContrasenaUsuario(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        if(!usuarioDAO.actualizarUltimaSesionUsuario(usuario.getIdUsuario())){
            throw new RuntimeException("No se pudo actualizar la última sesión");
        }
        return usuario;
    }

    public Usuario ingresarUsuario(Usuario admin,Usuario nuevoUsuario) {
        ValidarUsuario.validarAdmin(admin);
        validarUsuario(nuevoUsuario);
        if(usuarioDAO.buscarUsuarioPorEmail(nuevoUsuario.getEmail()).isPresent()){
            throw new RuntimeException("El email ya está registrado");
        }
        String contrasenaTemporal= GeneradorContrasena.generarContrasena(10);
        String contrasenaHash= hashContrasenaUsuario(contrasenaTemporal);

        nuevoUsuario.setContrasena(contrasenaHash);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setCambiarContrasena(true);

        if (!usuarioDAO.insertarUsuario(nuevoUsuario)) {
            throw new RuntimeException("No se pudo crear el usuario");
        }
        System.out.println("[SOLO DESARROLLO] Password temporal: " + contrasenaTemporal);
        return nuevoUsuario;

    }
    public boolean actualizarUsuario(Usuario admin, Usuario usuario){
        ValidarUsuario.validarAdmin(admin);
        validarUsuario(usuario);
        validarIdUsuairo(usuario.getIdUsuario());
        usuarioDAO.buscarUsuarioPorId(usuario.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario existente=usuarioDAO.buscarUsuarioPorEmail(usuario.getEmail()).orElse(null);
        if(existente !=null && existente.getIdUsuario()!=usuario.getIdUsuario()  ){
            throw new RuntimeException("El email ya está registrado");
        }
        boolean actualizado= usuarioDAO.actualizarUsuario(usuario);
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar el usuario");
        }
        return true;
    }
    public boolean cambiarActivoUsuario(Usuario admin,Usuario usuario){
        ValidarUsuario.validarAdmin(admin);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario a actualizar es obligatorio");
        }
        validarIdUsuairo(usuario.getIdUsuario());
        usuarioDAO.buscarUsuarioPorId(usuario.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        boolean actualizado=usuarioDAO.cambiarActivoUsuario(usuario.getIdUsuario(),usuario.getActivo());
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar el usuario");
        }
        return true;
    }
    public void cambiarContrasenaPrimerLoginUsuario(Usuario usuarioLogueado, String contrasenaNueva) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        Usuario usuario=obtenerUsuarioActivo(usuarioLogueado.getIdUsuario());
        if(!usuario.getCambiarContrasena()){
            throw new RuntimeException("No es necesario cambiar contraseña");
        }
        validarNuevaContrasenaUsuario(contrasenaNueva, usuario.getContrasena());
        actualizarContrasenaUsuario(usuario.getIdUsuario(),contrasenaNueva);
    }
    public void cambiarContrasenaUsuario(Usuario usuarioLogueado,String contrasenaActual, String contrasenaNueva) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        if(contrasenaActual==null || contrasenaActual.isBlank()){
            throw new IllegalArgumentException("La contraseña actual es obligatoria");
        }
        Usuario usuario=obtenerUsuarioActivo(usuarioLogueado.getIdUsuario());
        if(!validarContrasenaUsuario(contrasenaActual,usuario.getContrasena())){
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        validarNuevaContrasenaUsuario(contrasenaNueva, usuario.getContrasena());
        actualizarContrasenaUsuario(usuario.getIdUsuario(),contrasenaNueva);
    }
    private String hashContrasenaUsuario(String contrasena){

        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }
    private boolean validarContrasenaUsuario(String contrasena, String contrasenaHash){
        return BCrypt.checkpw(contrasena, contrasenaHash);
    }
    private void actualizarContrasenaUsuario(int id,String contrasena){
        validarIdUsuairo(id);
        String hashNuevaContrasena = hashContrasenaUsuario(contrasena);
        boolean actualizado=usuarioDAO.cambiarContrasenaUsuario(id,hashNuevaContrasena);
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar la contraseña");
        }
    }
    private Usuario obtenerUsuarioActivo(int id){
        validarIdUsuairo(id);
        Usuario usuario= usuarioDAO.buscarUsuarioPorIdLogin(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ValidarUsuario.validarUsuarioActivo(usuario);
        return usuario;
    }
    private void validarNuevaContrasenaUsuario(String contrasenaNueva, String contrasenaHash){
        if(contrasenaNueva==null || contrasenaNueva.isBlank()){
            throw new IllegalArgumentException("La contraseña nueva es obligatoria");
        }
        if(contrasenaNueva.length()<8){
            throw new IllegalArgumentException("La contraseña nueva debe tener al menos 8 caracteres");
        }
        if (validarContrasenaUsuario(contrasenaNueva, contrasenaHash)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta a la actual");
        }
    }
    private void validarIdUsuairo(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("Id inválido");
        }
    }
    private void validarUsuario(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException("Usuario inválido");
        }
        if(usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if(usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if(usuario.getRol()==null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }
}


