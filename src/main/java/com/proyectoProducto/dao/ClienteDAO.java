package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private static final String SQL_LISTAR="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente";
    private static final String SQL_BUSCAR_POR_ID="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente WHERE id_cliente=?";
    private static final String SQL_INSERTAR="INSERT INTO cliente (nombre, rut, email, telefono, activo) VALUES(?,?,?,?,?)";
    private static final String SQL_ACTUALIZAR="UPDATE cliente SET nombre=?,rut=?,email=?,telefono=?,activo=? WHERE id_cliente=?";
    private static final String SQL_CAMBIAR_ACTIVO="UPDATE cliente SET activo=? WHERE id_cliente=?";
    public Cliente mapearCliente(ResultSet rs) throws SQLException {
      return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getByte("activo")
        );
    }
    public void setCliente(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getRut());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4,cliente.getTelefono());
        stmt.setByte(5,cliente.getActivo());
    }
    public List<Cliente> listar(){
        List<Cliente> lista = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_LISTAR);
                ResultSet rs=stmt.executeQuery();
                ){
            while(rs.next()){
               lista.add(mapearCliente(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al listar los clientes",e);
        }
        return  lista;
    }
    public Cliente buscarPorId(int id){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_POR_ID)
                ){
            stmt.setInt(1,id);
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                if(rs.next()){
                    return mapearCliente(rs);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar por id",e);
        }
        return null;
    }
    public boolean insertar(Cliente cliente){
        try(
            Connection conn=ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR);
                ){
            setCliente(stmt,cliente);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw new RuntimeException("Error al insertar el cliente",e);
        }
    }
    public boolean actualizar(Cliente cliente){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR)
                ){
            setCliente(stmt,cliente);
            stmt.setInt(6,cliente.getIdCliente());
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar el cliente", e);

        }
    }
    public boolean cambiarActivo(int id, byte activo){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_ACTIVO)
                ){
            stmt.setByte(1,activo);
            stmt.setInt(2,id);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw new RuntimeException("Error al cambiar el activo",e);
        }
    }
}
