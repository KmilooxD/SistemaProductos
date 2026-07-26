package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetalleVentaDAO {
    private static final String SQL_LISTAR_DETALLEVENTA="SELECT id_detalleVenta,id_venta,id_producto,cantidad,precio_unitario FROM detalle_venta";
    private static final String SQL_LISTAR_DETALLEVENTA_POR_VENTA="SELECT id_detalleVenta,id_venta,id_producto,cantidad,precio_unitario FROM detalle_venta WHERE id_venta=?";
    private static final String SQL_INSERTAR_DETALLEVENTA="INSERT INTO detalle_venta (id_venta,id_producto,cantidad,precio_unitario) VALUES (?,?,?,?)";
    private DetalleVenta mapearDetalleVenta(ResultSet rs) throws SQLException {
        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setIdDetalleVenta(rs.getInt("id_detalleVenta"));
        detalleVenta.setIdVenta(rs.getInt("id_venta"));
        detalleVenta.setIdProducto(rs.getInt("id_producto"));
        detalleVenta.setCantidad(rs.getInt("cantidad"));
        detalleVenta.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        return detalleVenta;
    }
    private void setDetalleVenta(PreparedStatement stmt,DetalleVenta detalleVenta) throws SQLException {
                stmt.setInt(1,detalleVenta.getIdVenta());
                stmt.setInt(2,detalleVenta.getIdProducto());
                stmt.setInt(3,detalleVenta.getCantidad());
                stmt.setBigDecimal(4,detalleVenta.getPrecioUnitario());
    }
    public List<DetalleVenta> listarDetalleVenta() {
        List<DetalleVenta> lista = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_DETALLEVENTA);
                ){
            try(
                    ResultSet rs = stmt.executeQuery()
                    ){
                while(rs.next()){
                    lista.add(mapearDetalleVenta(rs));
                }
            }
        }catch(SQLException e){
        throw new RuntimeException("Error al listar detalle venta",e);
        }
        return lista;
    }
    public List<DetalleVenta> listarDetalleVentaPorVenta(int id_venta) {
        List<DetalleVenta> listaDetalleVentaPorVenta = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_DETALLEVENTA_POR_VENTA);
        ){
            stmt.setInt(1,id_venta);
            try(
                    ResultSet rs = stmt.executeQuery()
            ){
                while(rs.next()){
                    listaDetalleVentaPorVenta.add(mapearDetalleVenta(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al listar detalle venta",e);
        }
        return listaDetalleVentaPorVenta;
    }
    public boolean insertarDetalleVenta(DetalleVenta detalleVenta) {
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERTAR_DETALLEVENTA);
                ){
        setDetalleVenta(stmt,detalleVenta);
        return  stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al insertar detalle venta",e);
        }
    }
}
