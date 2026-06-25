package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaDAO {
private static final String SQL_LISTAR_VENTA="SELECT id_venta, fecha, total, id_usuario, id_cliente, activo FROM venta";
private static final String SQL_LISTAR_VENTAS_ACTIVAS="SELECT id_venta, fecha, total, id_usuario, id_cliente, activo FROM venta WHERE activo=1";
private static final String SQL_BUSCAR_VENTA_POR_ID="SELECT id_venta, fecha, total, id_usuario, id_cliente, activo FROM venta WHERE id_venta=?";
private static final String SQL_BUSCAR_VENTAS_POR_CLIENTE="SELECT id_venta, fecha, total, id_usuario, id_cliente, activo FROM venta WHERE id_cliente=?";
private static final String SQL_BUSCAR_VENTAS_POR_USUARIO="SELECT id_venta, fecha, total, id_usuario, id_cliente, activo FROM venta WHERE id_usuario=?";
private static final String SQL_INSERTAR_VENTA="INSERT INTO venta (total,id_usuario,id_cliente) VALUES (?,?,?)";
private static final String SQL_ACTUALIZAR_ACTIVO="UPDATE venta SET activo=? WHERE id_venta=?";
private Venta mapearVenta(ResultSet rs) throws SQLException {
    Venta venta = new Venta();
            venta.setIdVenta(rs.getInt("id_venta"));
            venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
            venta.setTotal(rs.getBigDecimal("total"));
            venta.setIdUsuario(rs.getInt("id_usuario"));
            venta.setIdCliente(rs.getInt("id_cliente"));
            venta.setActivo(rs.getBoolean("activo"));
            return venta;
}
private void setVenta(PreparedStatement stmt, Venta venta) throws SQLException {
    stmt.setBigDecimal(1, venta.getTotal());
    stmt.setInt(2, venta.getIdUsuario());
    stmt.setInt(3, venta.getIdCliente());
}
public List<Venta> listarVentas(){
    List<Venta> ventas = new ArrayList<>();
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_LISTAR_VENTA);
            ){
        try(
                ResultSet rs=stmt.executeQuery();
                ){
            while(rs.next()){
                ventas.add(mapearVenta(rs));
            }
        }
    }catch(SQLException e){
        throw new RuntimeException("Error al listar ventas",e);

    }
    return ventas;
}
public List<Venta> listarVentasActivas(){
    List<Venta> ventasActivos = new ArrayList<>();
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_LISTAR_VENTAS_ACTIVAS);

            ){
            try(
                    ResultSet rs=stmt.executeQuery();
               ){
                while(rs.next()) {
                    ventasActivos.add(mapearVenta(rs));
                }
            }
    }catch (SQLException e){
        throw new RuntimeException("Error al listar ventas",e);
    }
    return ventasActivos;
}
public Optional<Venta> buscarVentaPorId(int id){
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_VENTA_POR_ID);

       ){
        stmt.setInt(1, id);
        try(
                ResultSet rs = stmt.executeQuery();
        ){
            if(rs.next()){
                return Optional.of(mapearVenta(rs));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar venta por id",e);
    }
    return Optional.empty();
}
public List<Venta> buscarVentasPorCliente(int idCliente){
    List<Venta> ventasCliente = new ArrayList<>();
    try(
            Connection conn=ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_VENTAS_POR_CLIENTE);
            ){
        stmt.setInt(1, idCliente);
        try(
            ResultSet rs = stmt.executeQuery();
                ){
            while(rs.next()){
                ventasCliente.add(mapearVenta(rs));

            }
        }
    }catch (SQLException e){
        throw new RuntimeException("Error al buscar ventas por cliente",e);
    }
    return ventasCliente;
}
    public List<Venta> buscarVentasPorUsuario(int idUsuario){
    List<Venta> ventasUsuario = new ArrayList<>();
    try(
                Connection conn=ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_VENTAS_POR_USUARIO);
        ){
            stmt.setInt(1, idUsuario);
            try(
                    ResultSet rs = stmt.executeQuery();
            ){
                while(rs.next()){
                    ventasUsuario.add(mapearVenta(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar ventas por usuario",e);
        }
        return ventasUsuario;
    }
public boolean insertar(Venta venta){
    try(
        Connection conn= ConexionDB.getConection();
        PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR_VENTA);
            ){
        setVenta(stmt,venta);
        return stmt.executeUpdate()>0;
    }catch(SQLException e){
        throw new RuntimeException("Error al insertar venta",e);

    }
}
public boolean cambiarActivo(int idVenta, boolean activo){
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR_ACTIVO);
            ){
        stmt.setBoolean(1, activo);
        stmt.setInt(2, idVenta);
        return stmt.executeUpdate()>0;
    }catch (SQLException e){
        throw new RuntimeException("Error al cambiar activo",e);
    }
}

}
