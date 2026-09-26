package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAO {
    private static final String SQL_LISTAR_CATEGORIAS="SELECT id_categoria,nombre,descripcion, activo FROM categoria";
    private static final String SQL_BUSCAR_CATEGORIA_POR_ID="SELECT id_categoria,nombre,descripcion, activo FROM categoria WHERE id_categoria=?";
    private static final String SQL_BUSCAR_CATEGORIA_POR_NOMBRE="SELECT id_categoria,nombre,descripcion, activo FROM categoria WHERE nombre=?";
    private static final String SQL_INSERTAR_CATEGORIA="INSERT INTO categoria (nombre,descripcion) VALUES(?,?)";
    private static final String SQL_ACTUALIZAR_CATEGORIA="UPDATE categoria SET nombre=?,descripcion=? WHERE id_categoria=?";
    private static final String SQL_CAMBIAR_ACTIVO_CATEGORIA="UPDATE categoria SET activo=?  WHERE id_categoria=?";

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setDescripcion(rs.getString("descripcion"));
                categoria.setActivo(rs.getBoolean("activo"));
       return categoria;
    }
    private void setCategoria(PreparedStatement stmt, Categoria categoria) throws SQLException {
        stmt.setString(1, categoria.getNombre());
        stmt.setString(2, categoria.getDescripcion());
    }
    public List<Categoria> listarCategorias(){
        List<Categoria> categoria = new ArrayList<>();
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_LISTAR_CATEGORIAS);
                ResultSet rs=stmt.executeQuery();
        ){
            while(rs.next()){
                categoria.add(mapearCategoria(rs));
            }

        }catch(SQLException e){
            throw new RuntimeException("Error al listar categorias", e);
        }
        return categoria;
    }
    public Optional<Categoria> buscarCategoriaPorId(int id){
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_CATEGORIA_POR_ID);
                ){
            stmt.setInt(1, id);
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                if(rs.next()){
                 return  Optional.of(mapearCategoria(rs));
                }
            }

        }catch(SQLException e){
            throw new RuntimeException("Error al buscar por id", e);
        }
        return Optional.empty();
    }
    public Optional<Categoria>buscarCategoriaPorNombre(String nombre){
    try(
            Connection conn= ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_BUSCAR_CATEGORIA_POR_NOMBRE);
            ){
        stmt.setString(1, nombre);
        try(
                ResultSet rs=stmt.executeQuery();
                ){
            if(rs.next()){
                return Optional.of(mapearCategoria(rs));
            }
        }
        }catch(SQLException e){
        throw new RuntimeException("Error al buscar categoria por nombre", e);
        }
    return Optional.empty();
    }
    public Categoria agregarCategoria(Categoria categoria){
        try(
            Connection conn=ConexionDB.getConection();
            PreparedStatement stmt=conn.prepareStatement(SQL_INSERTAR_CATEGORIA)
        ){
            setCategoria(stmt, categoria);
            if(stmt.executeUpdate()==0){
                throw  new RuntimeException("Error al insertar la categoria");
            }
            return categoria;
        }catch(SQLException e){
            throw new RuntimeException("Error al insertar categoria", e);
        }
    }
    public Categoria actualizarCategoria(Categoria categoria){
        try(
                Connection conn= ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_ACTUALIZAR_CATEGORIA)
                ){
            setCategoria(stmt, categoria);
            stmt.setInt(3,categoria.getIdCategoria());
            if(stmt.executeUpdate()==0){
                throw  new RuntimeException("Error al actualizar la categoria");
            }
            return categoria;

        }catch(SQLException e){
         throw new RuntimeException("Error al actualizar categoria", e);
        }
    }
    public boolean actualizarActivoCategoria(int id, boolean activo){
        try(
                Connection conn =ConexionDB.getConection();
                PreparedStatement stmt=conn.prepareStatement(SQL_CAMBIAR_ACTIVO_CATEGORIA)
                ){
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            return  stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al cambiar activo",e);
        }
    }
}
