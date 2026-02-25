package com.proyectoProducto.model;

public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private int idCategoria;
    private byte estado;

    public Producto(int idProducto, String nombre, String descripcion, double precio, int stock, int idCategoria, byte estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
        this.estado = estado;
    }
    public Producto(String nombre, String descripcion, double precio, int stock, int idCategoria,byte estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
        this.estado = estado;
    }

    public int getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getIdCategoria() {
        return idCategoria;
    }
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
    public byte getEstado() {
        return estado;
    }
    public void setEstado(byte estado) {
        this.estado = estado;
    }
}
