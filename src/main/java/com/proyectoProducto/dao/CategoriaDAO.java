package com.proyectoProducto.dao;

import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private static final String SQL_LISTAR="SELECT id_categoria,nombre,descripcion FROM categoria";
    private static final String SQL_BUSCARPORID="SELECT id_categoria,nombre,descripcion FROM categoria Where id_categoria=?";
    private static final String SQL_INSERTAR="INSERT INTO categoria (nombre,descripcion) VALUES(?,?)";
    private static final String SQL_ACTUALIZAR="UPDATE categoria SET nombre=?,descripcion=? WHERE id_categoria=?";

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nombre"),
                rs.getString("descripcion"));
    }
    private void setCategoria(PreparedStatement stmt, Categoria categoria) throws SQLException {
        stmt.setString(1, categoria.getNombre());
        stmt.setString(2, categoria.getDescripcion());
    }
    public List<Categoria> listar(){
        List<Categoria> categoria = new ArrayList<>();
        try(
                Connection con= ConexionDB.getConection();
                PreparedStatement stmt=con.prepareStatement(SQL_LISTAR);
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
    public Categoria buscarPorId(int id){
        try(
                Connection con= ConexionDB.getConection();
                PreparedStatement stmt=con.prepareStatement(SQL_BUSCARPORID);
                ){
            stmt.setInt(1, id);
            try(
                    ResultSet rs=stmt.executeQuery();
                    ){
                if(rs.next()){
                 return  mapearCategoria(rs);
                }
            }

        }catch(SQLException e){
            throw new RuntimeException("Error al buscar por id", e);
        }
        return null;
    }
    public boolean insertar(Categoria categoria){
        try(
            Connection con=ConexionDB.getConection();
            PreparedStatement stmt=con.prepareStatement(SQL_INSERTAR)
        ){
            setCategoria(stmt, categoria);
            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            throw new RuntimeException("Error al insertar categoria", e);
        }
    }
    public boolean actualizar(Categoria categoria){
        try(
                Connection con= ConexionDB.getConection();
                PreparedStatement stmt=con.prepareStatement(SQL_ACTUALIZAR)
                ){
            setCategoria(stmt, categoria);
            stmt.setInt(3,categoria.getIdCategoria());
            return stmt.executeUpdate()>0;

        }catch(SQLException e){
         throw new RuntimeException("Error al actualizar categoria", e);
        }
    }
}
