package com.proyectoProducto.model;

import java.math.BigDecimal;

public class DetalleVenta {
    private int id_detalleVenta;
    private int id_venta;
    private int id_producto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public DetalleVenta(int id_detalleVenta, int id_venta, int id_producto, int cantidad, BigDecimal precioUnitario) {
        this.id_detalleVenta = id_detalleVenta;
        this.id_venta = id_venta;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    public DetalleVenta(int id_venta, int id_producto, int cantidad, BigDecimal precioUnitario) {
        this.id_venta = id_venta;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getId_detalleVenta() {
        return id_detalleVenta;
    }

    public void setId_detalleVenta(int id_detalleVenta) {
        this.id_detalleVenta = id_detalleVenta;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
