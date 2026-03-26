package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    private static final String SQL_LISTAR_USUARIO = "SELECT id_usuario, nombre, email, rol, activo, fecha_creacion, ultima_sesion FROM usuario";
    private static final String SQL_LISTAR_USUARIO_ACTIVO= "SELECT id_usuario, nombre, email, rol, activo, fecha_creacion, ultima_sesion FROM usuario WHERE activo=1";
    private static final String SQL_BUSCAR_POR_ID="SELECT id_usuario, nombre, email, rol, activo, fecha_creacion, ultima_sesion FROM usuario WHERE id_usuario=?";
    private static final String SQL_BUSCAR_POR_ID_LOGIN="SELECT id_usuario, nombre, email, contrasena, rol, activo, fecha_creacion, ultima_sesion, cambiar_contrasena FROM usuario WHERE id_usuario=?";
    private static final String SQL_BUSCAR_POR_EMAIL="SELECT id_usuario, nombre, email, rol, activo, fecha_creacion, ultima_sesion FROM usuario WHERE email=?";
    private static final String SQL_BUSCAR_POR_EMAIL_LOGIN="SELECT id_usuario, nombre, email, contrasena, rol, activo, fecha_creacion, ultima_sesion, cambiar_contrasena FROM usuario WHERE email=?";
    private static final String SQL_INSERTAR="INSERT INTO usuario (nombre, email, contrasena, rol) VALUES (?,?,?,?)";
    private static final String SQL_ACTUALIZAR="UPDATE usuario SET nombre=?, email=?, rol=?, activo=? WHERE id_usuario=?";
    private static final String SQL_CAMBIAR_ACTIVO="UPDATE usuario SET activo=? WHERE id_usuario=?";
    private static final String SQL_CAMBIAR_CONTRASENA="UPDATE usuario SET contrasena=?, cambiar_contrasena=0 WHERE id_usuario=?";
    private static final String SQL_ACTUALIZAR_ULTIMA_SESION="UPDATE usuario SET ultima_sesion= NOW() WHERE id_usuario=?";
    private Usuario mapearUsuarioCompleto(ResultSet rs) throws SQLException {
        Usuario usuario = mapearUsuarioBasico(rs);
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setCambiarContrasena(rs.getBoolean("cambiar_contrasena"));
        return usuario;
    }
    private Usuario mapearUsuarioBasico(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setRol( Usuario.Rol.valueOf(rs.getString("rol").toUpperCase()));
        usuario.setActivo(rs.getBoolean("activo"));
        Timestamp fechaCreacion=rs.getTimestamp("fecha_creacion");
        Timestamp ultimaSesion=rs.getTimestamp("ultima_sesion");
        if(fechaCreacion!=null){
            usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        if(ultimaSesion!=null){
            usuario.setUltimaSesion(ultimaSesion.toLocalDateTime());
        }
        return usuario;
    }
    private void setUsuarioInsertar(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNombre());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getContrasena());
        stmt.setString(4, usuario.getRol().name());
    }
    private void setUsuarioActualizar(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNombre());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getRol().name());
        stmt.setBoolean(4, usuario.getActivo());
        stmt.setInt(5,usuario.getIdUsuario());
    }
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_USUARIO);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                usuarios.add(mapearUsuarioBasico(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los usuarios",e);
        }
        return usuarios;
    }
    public List<Usuario> listarUsuariosActivo() {
        List<Usuario> usuarios = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_USUARIO_ACTIVO);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                usuarios.add(mapearUsuarioBasico(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los usuarios",e);
        }
        return usuarios;
    }
    public Optional<Usuario> buscarPorId(int id){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);

                ){
            stmt.setInt(1,id);
            try(
                    ResultSet rs = stmt.executeQuery();
                    ){
                if(rs.next()){
                    return Optional.of(mapearUsuarioBasico(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar el usuario",e);
        }
        return Optional.empty();
    }
    public Optional<Usuario> buscarPorIdLogin(int id){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID_LOGIN);

        ){
            stmt.setInt(1,id);
            try(
                    ResultSet rs = stmt.executeQuery();
            ){
                if(rs.next()){
                    return Optional.of(mapearUsuarioCompleto(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar el usuario",e);
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email){
        try(
            Connection conn=ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_POR_EMAIL);
                ){
            stmt.setString(1, email);
            try(
                    ResultSet rs = stmt.executeQuery();
            ){
                if(rs.next()){
                    return Optional.of(mapearUsuarioBasico(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar el usuario",e);
        }
        return Optional.empty();
    }
    public Optional<Usuario> buscarPorEmailLogin(String email){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_POR_EMAIL_LOGIN);
                ){
           stmt.setString(1, email);
           try(
                    ResultSet rs = stmt.executeQuery();
           ){
               if(rs.next()){
                   return Optional.of(mapearUsuarioCompleto(rs));
               }
           }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar el usuario",e);

        }
        return Optional.empty();
    }
    public boolean insertar(Usuario usuario){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR)
                ){
            setUsuarioInsertar(stmt,usuario);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw new RuntimeException("Error al insertar el usuario",e);
        }
    }
    public boolean actualizar(Usuario usuario){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR)
                ){
           setUsuarioActualizar(stmt,usuario);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw  new RuntimeException("Error al actualizar el usuario",e);
        }
    }
    public boolean cambiarActivo(int id,boolean activo){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_ACTIVO)
                ){
            stmt.setBoolean(1,activo);
            stmt.setInt(2,id);
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al cambiar activo",e);
        }
    }
    public boolean cambiarContrasena(int id,String contrasena){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_CONTRASENA)
                ){
            stmt.setString(1,contrasena);
            stmt.setInt(2,id);
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al cambiar contrasena",e);
        }
    }
    public boolean actualizarUltimaSesion(int id){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR_ULTIMA_SESION)
                ){
            stmt.setInt(1,id);
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar ultima sesion del usuario",e);

        }

    }

}