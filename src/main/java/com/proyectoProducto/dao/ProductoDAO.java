package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Producto;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ProductoDAO {
    private static final String SQL_LISTAR_PROUCTOS = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto";
    private static final String SQL_LISTAR_PROUCTOS_ACTIVOS = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE activo=1";
    private static final String SQL_BUSCAR_PRODUCTO_POR_ID = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE id_producto=?";
    private static final String SQL_BUSCAR_PRODUCTO_POR_NOMBRE= "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE nombre=?";
    private static final String SQL_INSERTAR_PRODUCTO = "INSERT INTO producto (nombre, descripcion, precio, stock, id_categoria) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR_PRODUCTO = "UPDATE producto SET nombre=?, descripcion=?, precio=?, stock=?, id_categoria=?, activo=? WHERE id_producto=?";
    private static final String SQL_CAMBIAR_ACTIVO_PRODUCTO = "UPDATE producto SET activo =? WHERE id_producto = ?";
    private static final String SQL_AGREGAR_STOCK_PRODUCTO= "UPDATE producto SET stock= stock + ? WHERE id_producto=?";
    private static final String SQL_DESCONTAR_STOCK_PRODUCTO= "UPDATE producto SET stock= stock - ? WHERE id_producto=?";
    private static final String SQL_ACTUALIZAR_STOCK_PRODUCTO= "UPDATE producto SET stock=? WHERE id_producto=?";
    private  Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setStock(rs.getInt("stock"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setActivo(rs.getBoolean("activo"));

        return producto;
    }
    private void setParametros(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombre());
        stmt.setString(2, producto.getDescripcion());
        stmt.setBigDecimal(3, producto.getPrecio());
        stmt.setInt(4, producto.getStock());
        stmt.setInt(5, producto.getIdCategoria());
        stmt.setBoolean(6, producto.getActivo());

    }
    public List<Producto> listarProductos(){
        List<Producto> productos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_PROUCTOS);
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
    public List<Producto> listarProdctosActivos(){
        List<Producto> productosActivos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_PROUCTOS_ACTIVOS);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                productosActivos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos activos", e);
        }
        return productosActivos;
    }
    public Optional<Producto> buscarProductoPorId(int id){
        try (
                Connection conn = ConexionDB.getConection();
        ) {
           return buscarProductoPorId(id, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por Id", e);
        }

    }
    public Optional<Producto> buscarProductoPorId(int id,Connection conn){
        try (

                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_PRODUCTO_POR_ID);
        ) {
            stmt.setInt(1, id);

            try (
                    ResultSet rs = stmt.executeQuery()
            ){
                if (rs.next()) {
                    return Optional.of(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por Id", e);
        }
        return Optional.empty();
    }
    public Optional<Producto> buscarProductoPorNombre(String nombre){
        try(
            Connection conn =ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_PRODUCTO_POR_NOMBRE);
                ){
                stmt.setString(1,nombre);
                try(
                        ResultSet rs=stmt.executeQuery()
                        ){
                    if(rs.next()){
                        return  Optional.of(mapearProducto(rs));
                    }
                }
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar producto por nombre",e);
        }
    return Optional.empty();
    }

    public boolean insertarProducto(Producto producto){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERTAR_PRODUCTO);
        ) {
            setParametros(stmt, producto);
            return stmt.executeUpdate()>0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar producto", e);
        }
    }
    public boolean actualizarProducto(Producto producto){
        try(Connection conn = ConexionDB.getConection();
            PreparedStatement stmt = conn.prepareStatement(SQL_ACTUALIZAR_PRODUCTO);)
        {
            setParametros(stmt, producto);
            stmt.setInt(7, producto.getIdProducto());
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }
    public  boolean actualizarActivoProducto(int id,boolean activo){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_CAMBIAR_ACTIVO_PRODUCTO)
        ) {
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar el activo", e);
        }
    }
    public boolean agregarStockProducto(int id, int stock){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_AGREGAR_STOCK_PRODUCTO)
                ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar stock",e);
        }
    }
    public boolean descontarStockProducto(int id, int stock, Connection conn){
        try(
                PreparedStatement stmt= conn.prepareStatement(SQL_DESCONTAR_STOCK_PRODUCTO)
        ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al descontar stock",e);
        }
    }
    public boolean actualizarStockProducto(int id, int stock){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_ACTUALIZAR_STOCK_PRODUCTO)
        ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar stock",e);
        }
    }

}
