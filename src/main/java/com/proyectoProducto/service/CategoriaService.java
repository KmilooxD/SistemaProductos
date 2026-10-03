package com.proyectoProducto.service;

import com.proyectoProducto.dao.CategoriaDAO;
import com.proyectoProducto.model.Categoria;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.util.FormatearTexto;
import com.proyectoProducto.util.ValidarUsuario;

import java.util.List;
import java.util.Optional;

public class CategoriaService {
  private final CategoriaDAO categoriaDAO;

  public CategoriaService(CategoriaDAO categoriaDAO) {
    this.categoriaDAO = categoriaDAO;

  }
  public List<Categoria> listarCategorias(){
      return categoriaDAO.listarCategorias() ;
  }
  public Categoria buscarCategoriaPorId(int id){
     validarIdCategoria(id,"categoría");
      return categoriaDAO.buscarCategoriaPorId(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
  }
  public Categoria buscarCategoriaPorNombre(String nombre){
      if(nombre==null || nombre.isBlank()){
          throw new IllegalArgumentException("El nombre es obligatorio");
      }
      nombre=FormatearTexto.formatearNombre(nombre);
      return categoriaDAO.buscarCategoriaPorNombre(nombre).orElseThrow(() -> new RuntimeException("Nombre de la categoría no encontrada"));
  }
  public Categoria ingresarCategoria(Usuario admin, Categoria categoria){
      ValidarUsuario.validarAdmin(admin);
      validarCategoria(categoria);
      categoria.setActivo(true);
      return categoriaDAO.insertarCategoria(categoria);
  }
  public Categoria actualizarCategoria(Usuario admin, Categoria categoria){
      ValidarUsuario.validarAdmin(admin);
      validarCategoria(categoria);
      categoriaDAO.buscarCategoriaPorId(categoria.getIdCategoria()).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
      Optional<Categoria> nombreExistente=categoriaDAO.buscarCategoriaPorNombre(categoria.getNombre());
      if(nombreExistente.isPresent() && nombreExistente.get().getIdCategoria()!= categoria.getIdCategoria()){
          throw new IllegalArgumentException("El nombre de la categoría ya existe");
      }

      return categoriaDAO.actualizarCategoria(categoria);
  }
  public boolean cambiarActivoCategoria(Usuario admin, int id, boolean activo){
      ValidarUsuario.validarAdmin(admin);
      validarIdCategoria(id,"categoría");
      categoriaDAO.buscarCategoriaPorId(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
      if(!categoriaDAO.actualizarActivoCategoria(id,activo)){
          throw new RuntimeException("Error al actualizar el activo");
      }
      return true;
  }
  private void validarCategoria(Categoria categoria){
      if(categoria==null){
          throw new IllegalArgumentException("La categoría es obligatoria");
      }
      if (categoria.getNombre()==null ||categoria.getNombre().isBlank()){
          throw new IllegalArgumentException("El Nombre es obligatorio");
      }
      categoria.setNombre(FormatearTexto.formatearNombre(categoria.getNombre()));
      if(categoria.getNombre().length()>100){
          throw new IllegalArgumentException("Has sobrepasado el límite de caracteres (100)");
      }
      if (categoria.getDescripcion()==null || categoria.getDescripcion().isBlank()){
          throw new IllegalArgumentException("La Descripción es obligatoria");
      }
      if(categoria.getDescripcion().length()>255){
          throw new IllegalArgumentException("Has sobrepasado el límite de descripción (255)");
      }
}
  private void validarIdCategoria(int id, String entidad){
        if(id<=0){
            throw new IllegalArgumentException("Id "+entidad+" inválido");
        }
    }
}
