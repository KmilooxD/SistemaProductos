package com.proyectoProducto.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Venta {
    private int id_venta;
    private LocalDateTime fecha;
    private BigDecimal total;
    private int id_usuario;
    private int id_cliente;

    public Venta(int id_venta, LocalDateTime fecha, BigDecimal total, int id_usuario, int id_cliente) {
        this.id_venta = id_venta;
        this.fecha = fecha;
        this.total = total;
        this.id_usuario = id_usuario;
        this.id_cliente = id_cliente;
    }
    public Venta(BigDecimal total, int id_usuario, int id_cliente) {
        this.total = total;
        this.id_usuario = id_usuario;
        this.id_cliente = id_cliente;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }
}
