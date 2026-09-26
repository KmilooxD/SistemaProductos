package com.proyectoProducto.model;

public class Categoria {
   private int idCategoria;
   private String nombre;
   private String descripcion;
   private boolean activo;

   public Categoria() {

   }

   public int getIdCategoria(){
       return this.idCategoria;
   }
   public void setIdCategoria(int idCategoria){
       this.idCategoria = idCategoria;
   }
   public String getNombre(){
       return this.nombre;
   }
   public void setNombre(String nombre){
       this.nombre = nombre;
   }
   public String getDescripcion(){
       return this.descripcion;
   }
   public void setDescripcion(String descripcion){
       this.descripcion = descripcion;
   }
    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
