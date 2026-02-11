package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Producto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ProductoDAO {
private static final String SQL_LISTAR = "SELECT id_producto,nombre,descripcion,precio,stock,estado,id_categoria FROM producto";
private static final String SQL_BUSCAR_POR_ID = "SELECT id_producto,nombre,descripcion,precio,stock,estado,id_categoria FROM producto WHERE id_producto=?";
private static final String SQL_INSERTAR = "INSERT INTO producto "+"(nombre, descripcion, precio, stock, id_categoria, estado) "+"VALUES (?, ?, ?, ?, ?, ?)";
private static final String SQL_ACTUALIZAR = "UPDATE producto SET nombre=?,descripcion=?,precio=?,stock=?,id_categoria=?,estado=? WHERE id_producto=?";
private static final String SQL_CAMBIAR_ESTADO = "UPDATE producto SET estado =? WHERE id_producto = ?";

    private  Producto mapearProducto(ResultSet rs) throws SQLException {
return new Producto(
        rs.getInt("id_producto"),
        rs.getString("nombre"),
        rs.getString("descripcion"),
        rs.getDouble("precio"),
        rs.getInt("stock"),
        rs.getInt("id_categoria"),
        rs.getByte("estado")
        );
    }
    private void setParametros(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombre());
        stmt.setString(2, producto.getDescripcion());
        stmt.setDouble(3, producto.getPrecio());
        stmt.setInt(4, producto.getStock());
        stmt.setInt(5, producto.getIdCategoria());
        stmt.setByte(6, producto.getEstado());
    }
    public List<Producto> listar(){
        List<Producto> productos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
             productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
        return productos;
    }
    public Producto buscarPorId(int id){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
               return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por Id", e);
        }
        return null;
    }
    public boolean insertar(Producto producto){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERTAR);
        ) {
            setParametros(stmt, producto);
            return stmt.executeUpdate()>0;

        } catch (SQLException e) {
       throw new RuntimeException("Error al insertar producto", e);
        }
    }
   public boolean actualizar(Producto producto){
        try(Connection conn = ConexionDB.getConection();
            PreparedStatement stmt = conn.prepareStatement(SQL_ACTUALIZAR);)
        {
           setParametros(stmt, producto);
            stmt.setInt(7, producto.getIdProducto());
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }
    public  boolean cambiarEstado(int id,byte estado){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_CAMBIAR_ESTADO);
        ) {
            stmt.setByte(1, estado);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado", e);
        }
    }
    }
