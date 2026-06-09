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
        return usuarioDAO.listarUsuariosActivo();
    }

    public Usuario buscarPorId(int id){
        validarId(id);
        return usuarioDAO.buscarPorId(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public Usuario buscarPorEmail(String email){
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return usuarioDAO.buscarPorEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public Usuario validarLogin(String email, String contrasena) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        Usuario usuario = usuarioDAO.buscarPorEmailLogin(email).orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
        ValidarUsuario.validarUsuarioActivo(usuario);

        if (!validarContrasena(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        if(!usuarioDAO.actualizarUltimaSesion(usuario.getIdUsuario())){
            throw new RuntimeException("No se puedo actualizar la ultima sesion");
        }
        return usuario;
    }

    public Usuario crearUsuario(Usuario admin,Usuario nuevoUsuario) {
        ValidarUsuario.validarAdmin(admin);
        validarUsuario(nuevoUsuario);
        if(usuarioDAO.buscarPorEmail(nuevoUsuario.getEmail()).isPresent()){
            throw new RuntimeException("El email ya está registrado");
        }
        String contrasenaTemporal= GeneradorContrasena.generarContrasena(10);
        String contrasenaHash= hashContrasena(contrasenaTemporal);

        nuevoUsuario.setContrasena(contrasenaHash);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setCambiarContrasena(true);

        if (!usuarioDAO.insertar(nuevoUsuario)) {
            throw new RuntimeException("No se pudo crear el usuario");
        }
        System.out.println("[SOLO DESARROLLO] Password temporal: " + contrasenaTemporal);
        return nuevoUsuario;

    }
    public boolean actualizarUsuario(Usuario admin, Usuario usuario){
        ValidarUsuario.validarAdmin(admin);
        validarUsuario(usuario);
        validarId(usuario.getIdUsuario());
        usuarioDAO.buscarPorId(usuario.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario existente=usuarioDAO.buscarPorEmail(usuario.getEmail()).orElse(null);
        if(existente !=null && existente.getIdUsuario()!=usuario.getIdUsuario()  ){
            throw new RuntimeException("El email ya está registrado");
        }
        boolean actualizado= usuarioDAO.actualizar(usuario);
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar el usuario");
        }
        return true;
    }
    public boolean cambiarActivo(Usuario admin,Usuario usuario){
        ValidarUsuario.validarAdmin(admin);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario a actualizar es obligatorio");
        }
        validarId(usuario.getIdUsuario());
        usuarioDAO.buscarPorId(usuario.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        boolean actualizado=usuarioDAO.cambiarActivo(usuario.getIdUsuario(),usuario.getActivo());
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar el usuario");
        }
        return true;
    }
    public void cambiarContrasenaPrimerLogin(Usuario usuarioLogueado, String contrasenaNueva) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        Usuario usuario=obtenerUsuarioActivo(usuarioLogueado.getIdUsuario());
        if(!usuario.getCambiarContrasena()){
            throw new RuntimeException("No es necesario cambiar contraseña");
        }
        validarNuevaContrasena(contrasenaNueva, usuario.getContrasena());
        actualizarContrasena(usuario.getIdUsuario(),contrasenaNueva);
    }
    public void cambiarContrasena(Usuario usuarioLogueado,String contrasenaActual, String contrasenaNueva) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        if(contrasenaActual==null || contrasenaActual.isBlank()){
            throw new IllegalArgumentException("La contraseña actual es obligatoria");
        }
        Usuario usuario=obtenerUsuarioActivo(usuarioLogueado.getIdUsuario());
        if(!validarContrasena(contrasenaActual,usuario.getContrasena())){
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        validarNuevaContrasena(contrasenaNueva, usuario.getContrasena());
        actualizarContrasena(usuario.getIdUsuario(),contrasenaNueva);
    }
    private String hashContrasena(String contrasena){

        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }
    private boolean validarContrasena(String contrasena, String contrasenaHash){
        return BCrypt.checkpw(contrasena, contrasenaHash);
    }
    private void actualizarContrasena(int id,String contrasena){
        validarId(id);
        String hashNuevaContrasena = hashContrasena(contrasena);
        boolean actualizado=usuarioDAO.cambiarContrasena(id,hashNuevaContrasena);
        if(!actualizado){
            throw new RuntimeException("No se pudo actualizar la contrasena");
        }
    }
    private Usuario obtenerUsuarioActivo(int id){
        validarId(id);
        Usuario usuario= usuarioDAO.buscarPorIdLogin(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ValidarUsuario.validarUsuarioActivo(usuario);
        return usuario;
    }
    private void validarNuevaContrasena(String contrasenaNueva, String contrasenaHash){
        if(contrasenaNueva==null || contrasenaNueva.isBlank()){
            throw new IllegalArgumentException("La contraseña nueva es obligatoria");
        }
        if(contrasenaNueva.length()<8){
            throw new IllegalArgumentException("La contraseña nueva debe tener al menos 8 caracteres");
        }
        if (validarContrasena(contrasenaNueva, contrasenaHash)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta a la actual");
        }
    }
    private void validarId(int id){
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


