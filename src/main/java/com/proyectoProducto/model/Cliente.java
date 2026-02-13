package com.proyectoProducto.model;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private byte activo;

    public Cliente(int idCliente,String nombre,String rut,String email,String telefono,byte activo){
        this.idCliente=idCliente;
        this.nombre=nombre;
        this.rut=rut;
        this.email=email;
        this.telefono=telefono;
        this.activo=activo;
    }
    public Cliente(String nombre,String rut,String email,String telefono,byte activo){
        this.nombre=nombre;
        this.rut=rut;
        this.email=email;
        this.telefono=telefono;
        this.activo=activo;
    }

    public int getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRut() {
        return rut;
    }
    public void setRut(String rut) {
        this.rut = rut;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public byte getActivo() {
        return activo;
    }
    public void setActivo(byte activo) {
        this.activo = activo;
    }
}
