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
    private static final String SQL_LISTAR_PROUCTO = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto";
    private static final String SQL_LISTAR_PROUCTO_ACTIVO = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE activo=1";
    private static final String SQL_BUSCAR_POR_ID = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE id_producto=?";
    private static final String SQL_BUSCAR_POR_NOMBRE= "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria, activo FROM producto WHERE nombre=?";
    private static final String SQL_INSERTAR = "INSERT INTO producto (nombre, descripcion, precio, stock, id_categoria) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR = "UPDATE producto SET nombre=?, descripcion=?, precio=?, stock=?, id_categoria=?, activo=? WHERE id_producto=?";
    private static final String SQL_CAMBIAR_ACTIVO = "UPDATE producto SET activo =? WHERE id_producto = ?";
    private static final String SQL_AGREGAR_STOCK= "UPDATE producto SET stock= stock + ? WHERE id_producto=?";
    private static final String SQL_DESCONTAR_STOCK= "UPDATE producto SET stock= stock- ? WHERE id_producto=?";
    private static final String SQL_ACTUALIZAR_STOCK= "UPDATE producto SET stock=? WHERE id_producto=?";
    private  Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setStock(rs.getInt("stock"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setActivo(rs.getBoolean("activo"));

        return producto;
    }
    private void setParametros(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombre());
        stmt.setString(2, producto.getDescripcion());
        stmt.setDouble(3, producto.getPrecio());
        stmt.setInt(4, producto.getStock());
        stmt.setInt(5, producto.getIdCategoria());
        stmt.setBoolean(6, producto.getActivo());

    }
    public List<Producto> listarProductos(){
        List<Producto> productos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_PROUCTO);
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
    public List<Producto> listarProdctosActivo(){
        List<Producto> productosActivos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_PROUCTO_ACTIVO);
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
    public Optional<Producto> buscarPorId(int id){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);
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
            throw new RuntimeException("Error al buscar por Id", e);
        }
        return Optional.empty();
    }
    public Optional<Producto> buscarPorNombre(String nombre){
        try(
            Connection conn =ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_POR_NOMBRE);
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
            throw new RuntimeException("Error al buscar por nombre",e);
        }
    return Optional.empty();
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
    public  boolean cambiarActivo(int id,boolean activo){
        try (
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt = conn.prepareStatement(SQL_CAMBIAR_ACTIVO)
        ) {
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado", e);
        }
    }
    public boolean agregarStock(int id, int stock){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_AGREGAR_STOCK)
                ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar stock",e);
        }
    }
    public boolean descontarStock(int id, int stock){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_DESCONTAR_STOCK)
        ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al descontar stock",e);
        }
    }
    public boolean actualizarStock(int id, int stock){
        try(
                Connection conn = ConexionDB.getConection();
                PreparedStatement stmt= conn.prepareStatement(SQL_ACTUALIZAR_STOCK)
        ){
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            return stmt.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar stock",e);
        }
    }

}
