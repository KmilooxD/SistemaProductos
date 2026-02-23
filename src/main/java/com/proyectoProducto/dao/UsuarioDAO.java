package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private static final String SQL_LISTAR = "SELECT id_usuario,nombre,email,rol,estado FROM usuario";
    private static final String SQL_BUSCAR_POR_ID="SELECT id_usuario,nombre,email,rol,estado FROM usuario where id_usuario=?";
    private static final String SQL_INSERTAR="INSERT INTO usuario (nombre,email,contrasena,rol,estado) VALUES (?,?,?,?,?)";
    private static final String SQL_ACTUALIZAR="UPDATE usuario SET nombre=?,email=?,rol=?,estado=? WHERE id_usuario=?";
    private static final String SQL_CAMBIAR_ESTADO="UPDATE usuario SET estado=? WHERE id_usuario=?";
    private static final String SQL_CAMBIAR_CONTRASENA="UPDATE usuario SET contrasena=? WHERE id_usuario=?";
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("email"),
                Usuario.Rol.valueOf(rs.getString("rol")),
                rs.getByte("estado")
        );
    }
    private void setUsuarioInsertar(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNombre());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getContrasena());
        stmt.setString(4, usuario.getRol().name());
        stmt.setByte(5, usuario.getEstado());
    }
    private void setUsuarioActualizar(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNombre());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getRol().name());
        stmt.setByte(4, usuario.getEstado());
        stmt.setInt(5,usuario.getIdUsuario());
    }
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los usuarios",e);
        }
        return usuarios;
    }
    public Usuario buscarPorId(int id){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);

                ){
            stmt.setInt(1,id);
            try(
                    ResultSet rs = stmt.executeQuery();
                    ){
                if(rs.next()){
                    return mapearUsuario(rs);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar el usuario",e);
        }
        return null;
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
    public boolean cambiarEstado(int id,byte estado){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_ESTADO)
                ){
            stmt.setByte(1,estado);
            stmt.setInt(2,id);
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al cambiar estado",e);
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

}