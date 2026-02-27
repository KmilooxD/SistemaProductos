package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
private static final String SQL_LISTAR="SELECT id_venta,fecha,total,id_usuario,id_cliente FROM venta";
private static final String SQL_BUSCAR_POR_ID="SELECT id_venta,fecha,total,id_usuario,id_cliente FROM venta WHERE id_venta=?";
private static final String SQL_INSERTAR="INSERT INTO venta (total,id_usuario,id_cliente) VALUES (?,?,?)";
private Venta mapearVenta(ResultSet rs) throws SQLException {
    return new Venta(
            rs.getInt("id_venta"),
            rs.getTimestamp("fecha").toLocalDateTime(),
            rs.getBigDecimal("total"),
            rs.getInt("id_usuario"),
            rs.getInt("id_cliente")
    );
}
private void setVenta(PreparedStatement stmt, Venta venta) throws SQLException {
    stmt.setBigDecimal(1, venta.getTotal());
    stmt.setInt(2, venta.getId_usuario());
    stmt.setInt(3, venta.getId_cliente());
}
public List<Venta> listar(){
    List<Venta> ventas = new ArrayList<>();
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_LISTAR);
            ResultSet rs=stmt.executeQuery();
            ){
            while(rs.next()){
                ventas.add(mapearVenta(rs));
            }

    }catch(SQLException e){
        throw new RuntimeException("Error al listar ventas",e);

    }
    return ventas;
}
public Venta buscarPorId(int id){
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_POR_ID);
            ){
        stmt.setInt(1, id);
        try(
                ResultSet rs = stmt.executeQuery();
        ){
            if(rs.next()){
                return mapearVenta(rs);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar venta por id",e);
    }
    return null;
}
public boolean insertar(Venta venta){
    try(
        Connection conn= ConexionDB.getConection();
        PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR);
            ){
        setVenta(stmt,venta);
        return stmt.executeUpdate()>0;
    }catch(SQLException e){
        throw new RuntimeException("Error al insertar venta",e);

    }
}

}
