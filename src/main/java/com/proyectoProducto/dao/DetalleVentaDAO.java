package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO {
    private static final String SQL_LISTAR="SELECT id_detalle,id_venta,id_producto,cantidad,precio_unitario FROM detalle_venta";
    private static final String SQL_BUSCAR_POR_ID="SELECT id_detalle,id_venta,id_producto,cantidad,precio_unitario FROM detalle_venta WHERE id_producto=?";
    private static final String SQL_INSERTAR="INSERT INTO detalle_venta (id_venta,id_producto,cantidad,precio_unitario) VALUES (?,?,?,?)";
    private DetalleVenta mapearDetalleVenta(ResultSet rs) throws SQLException {
        return new DetalleVenta(
                rs.getInt("id_detalle"),
                rs.getInt("id_venta"),
                rs.getInt("id_producto"),
                rs.getInt("cantidad"),
                rs.getBigDecimal("precio_unitario")
        );
    }
    private void setDetalleVenta(PreparedStatement stmt,DetalleVenta detalleVenta) throws SQLException {
                stmt.setInt(1,detalleVenta.getId_venta());
                stmt.setInt(2,detalleVenta.getId_producto());
                stmt.setInt(3,detalleVenta.getId_producto());
                stmt.setBigDecimal(4,detalleVenta.getPrecioUnitario());
    }
    public List<DetalleVenta> listar() {
        List<DetalleVenta> lista = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR);
                ResultSet rs = stmt.executeQuery()
                ){
            while(rs.next()){
                lista.add(mapearDetalleVenta(rs));
            }

        }catch(SQLException e){
        throw new RuntimeException("Error al listar detalle venta",e);
        }
        return lista;
    }
    public DetalleVenta buscarPorId(int id) {
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);
                ){
            stmt.setInt(1, id);
           try(
                   ResultSet rs = stmt.executeQuery()
                   ){
               if(rs.next()){
                   return mapearDetalleVenta(rs);
               }
           }
        }catch(SQLException e){
            throw new RuntimeException("Error al buscar detalle venta por id",e);
        }
        return null;
    }
    public boolean insertar(DetalleVenta detalleVenta) {
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERTAR);
                ){
        setDetalleVenta(stmt,detalleVenta);
        return  stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al insertar detalle venta",e);
        }
    }
}
