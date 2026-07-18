package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAO {
    private static final String SQL_LISTAR_CLIENTES="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente";
    private static final String SQL_LISTAR_CLIENTES_ACTIVOS="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente WHERE activo=1";
    private static final String SQL_BUSCAR_CLIENTE_POR_ID="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente WHERE id_cliente=?";
    private static final String SQL_BUSCAR_CLIENTE_POR_RUT="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente WHERE rut=?";
    private static final String SQL_BUSCAR_CLIENTE_POR_EMAIL="SELECT id_cliente, nombre, rut, email, telefono, activo FROM cliente WHERE email=?";
    private static final String SQL_INSERTAR_CLIENTE="INSERT INTO cliente (nombre, rut, email, telefono, activo) VALUES(?,?,?,?,?)";
    private static final String SQL_ACTUALIZAR_CLIENTE="UPDATE cliente SET nombre=?,rut=?,email=?,telefono=? WHERE id_cliente=?";
    private static final String SQL_CAMBIAR_ACTIVO="UPDATE cliente SET activo=? WHERE id_cliente=?";
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
      Cliente cliente = new Cliente();
      cliente.setIdCliente(rs.getInt("id_cliente"));
      cliente.setNombre(rs.getString("nombre"));
      cliente.setRut(rs.getString("rut"));
      cliente.setEmail(rs.getString("email"));
      cliente.setTelefono(rs.getString("telefono"));
      cliente.setActivo(rs.getBoolean("activo"));

      return cliente;
    }
    private void setCliente(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getRut());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4,cliente.getTelefono());

    }
    public List<Cliente> listarClientes(){
        List<Cliente> listaClientes = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_LISTAR_CLIENTES);

                ){
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                while(rs.next()){
                    listaClientes.add(mapearCliente(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al listar los clientes",e);
        }
        return  listaClientes;
    }
    public List<Cliente> listarClientesActivos(){
        List<Cliente> listaClientesActivos = new ArrayList<>();
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_LISTAR_CLIENTES_ACTIVOS);

                ){
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                while(rs.next()){
                    listaClientesActivos.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes activos",e);
        }
        return  listaClientesActivos;
    }
    public Optional<Cliente> buscarClientePorId(int id){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_CLIENTE_POR_ID)
                ){
            stmt.setInt(1,id);
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                if(rs.next()){
                    return Optional.of(mapearCliente(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar por id",e);
        }
        return Optional.empty();
    }
    public Optional<Cliente> buscarClientePorRut(String rut){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_CLIENTE_POR_RUT)
                ){
            stmt.setString(1, rut);
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                if(rs.next()){
                    return Optional.of(mapearCliente(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar por rut",e);
        }
        return Optional.empty();
    }
    public Optional<Cliente> buscarClientePorEmail(String email){
         try(
                 Connection conn=ConexionDB.getConection();
                 PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_CLIENTE_POR_EMAIL)
                 ){
             stmt.setString(1, email);
             try(
                     ResultSet rs=stmt.executeQuery();
                     ){
                 if(rs.next()){
                     return Optional.of(mapearCliente(rs));
                 }
             }
         }catch (SQLException e){
             throw  new RuntimeException("Error al buscar por email",e);
         }
         return Optional.empty();
    }
    public boolean insertarCliente(Cliente cliente){
        try(
            Connection conn=ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR_CLIENTE);
                ){
            setCliente(stmt,cliente);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw new RuntimeException("Error al insertar el cliente",e);
        }
    }
    public boolean actualizarCliente(Cliente cliente){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR_CLIENTE)
                ){
            setCliente(stmt,cliente);
            stmt.setInt(5,cliente.getIdCliente());
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar el cliente", e);

        }
    }
    public boolean actualizarActivo(int id, boolean activo){
        try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_ACTIVO)
                ){
            stmt.setBoolean(1,activo);
            stmt.setInt(2,id);
            return stmt.executeUpdate()>0;
        }catch (SQLException e){
            throw new RuntimeException("Error al cambiar el activo",e);
        }
    }
}
