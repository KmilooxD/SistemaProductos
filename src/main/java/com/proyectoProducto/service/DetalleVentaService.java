package com.proyectoProducto.service;

import com.proyectoProducto.dao.DetalleVentaDAO;
import com.proyectoProducto.dao.ProductoDAO;
import com.proyectoProducto.dao.VentaDAO;
import com.proyectoProducto.model.DetalleVenta;
import com.proyectoProducto.model.Producto;
import com.proyectoProducto.model.Venta;

import java.sql.Connection;
import java.util.List;


public class DetalleVentaService {
    private final DetalleVentaDAO detalleVentaDAO;
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private static final String VENTA = "venta";
    private static final String PRODUCTO = "producto";
    private static final String DETALLE_VENTA = "detalle de venta";
    public DetalleVentaService(DetalleVentaDAO detalleVentaDAO, VentaDAO ventaDAO, ProductoDAO productoDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
    }

    public List<DetalleVenta> listarDetallesVentas() {
        return detalleVentaDAO.listarDetalleVentas();
        }
    public List<DetalleVenta> listarDetallesVentasPorVenta(int id) {
        validarIdDetalleVenta(id,DETALLE_VENTA);
        return detalleVentaDAO.listarDetalleVentasPorVenta(id);
    }
    public boolean ingresarDetalleVenta(DetalleVenta detalleVenta, Connection conn){
    if(detalleVenta==null){
    throw new IllegalArgumentException("El detalle de venta no puede ser null.");
    }
    validarIdDetalleVenta(detalleVenta.getIdVenta(),VENTA);
    validarIdDetalleVenta(detalleVenta.getIdProducto(),PRODUCTO);
    Venta venta=ventaDAO.buscarVentaPorId(detalleVenta.getIdVenta(),conn).orElseThrow(()->new IllegalArgumentException("La venta no existe"));
    Producto producto=productoDAO.buscarProductoPorId(detalleVenta.getIdProducto(),conn).orElseThrow(()->new IllegalArgumentException("El producto no existe"));
    validarVentaActiva(venta);
    validarProductoActivo(producto);
    if (detalleVenta.getCantidad()<=0){
        throw new IllegalArgumentException("Cantidad inválida");
    }
    detalleVenta.setPrecioUnitario(producto.getPrecio());
    if(!detalleVentaDAO.insertarDetalleVenta(detalleVenta,conn)){
        throw new RuntimeException("No se pudo insertar el detalle de venta");
    }
        return true;
    }

    private void validarIdDetalleVenta(int id, String entidad){
        if(id<=0){
            throw new IllegalArgumentException("Id "+entidad+" inválido");
        }
    }
    private void validarProductoActivo(Producto producto){
        if(!producto.getActivo()){
            throw new IllegalArgumentException("El producto esta inactivo");
        }
    }
    private void validarVentaActiva(Venta venta){
        if(!venta.getActivo()){
            throw new IllegalArgumentException("La venta esta inactiva");
        }
    }
}
