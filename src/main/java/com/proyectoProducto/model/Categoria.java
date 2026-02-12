package com.proyectoProducto.model;

public class Categoria {
   private int idCategoria;
   private String nombre;
   private String descripcion;

   public Categoria(int idCategoria, String nombre, String descripcion) {
       this.idCategoria = idCategoria;
       this.nombre = nombre;
       this.descripcion = descripcion;
   }
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
   public int getIdCategoria(){
       return this.idCategoria;
   }
   public void setIdCAtegoria(int idCategoria){
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
}
