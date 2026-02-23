package com.proyectoProducto.model;

import java.time.LocalDateTime;


public class Usuario {
    private int idUsuario;
    private String nombre;
    private String email;
    private String contrasena;
    private Rol rol;
    private byte estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaSesion;

    public enum Rol
    {
        ADMIN,
        VENDEDOR
    }

    public Usuario(int idUsuario,String nombre,String email,Rol rol, byte estado){
        this.idUsuario=idUsuario;
        this.nombre=nombre;
        this.email=email;
        this.rol=rol;
        this.estado=estado;
    }
  public Usuario(String nombre, String email,String contrasena, Rol rol, byte estado){
        this.nombre=nombre;
        this.email=email;
        this.contrasena=contrasena;
        this.rol=rol;
        this.estado=estado;
    }
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(LocalDateTime ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }
}
