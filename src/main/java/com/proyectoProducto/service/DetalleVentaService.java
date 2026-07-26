package com.proyectoProducto.service;

import com.proyectoProducto.dao.DetalleVentaDAO;
import com.proyectoProducto.dao.ProductoDAO;
import com.proyectoProducto.dao.VentaDAO;
import com.proyectoProducto.model.DetalleVenta;
import com.proyectoProducto.model.Producto;
import com.proyectoProducto.model.Venta;
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

    public List<DetalleVenta> listarDetalleVentas() {
        return detalleVentaDAO.listarDetalleVenta();
        }
    public List<DetalleVenta> listarDetalleVentasPorVenta(int id) {
        validarId(id,DETALLE_VENTA);
        return detalleVentaDAO.listarDetalleVentaPorVenta(id);
    }
    public boolean crearDetalleVenta(DetalleVenta detalleVenta){
    if(detalleVenta==null){
    throw new IllegalArgumentException("El detalle de venta no puede ser null.");
    }
    validarId(detalleVenta.getIdVenta(),VENTA);
    validarId(detalleVenta.getIdProducto(),PRODUCTO);
    Venta venta=ventaDAO.buscarVentaPorId(detalleVenta.getIdVenta()).orElseThrow(()->new IllegalArgumentException("La venta no existe"));
    Producto producto=productoDAO.buscarPorId(detalleVenta.getIdProducto()).orElseThrow(()->new IllegalArgumentException("El producto no existe"));
    validarVentaActiva(venta);
    validarProductoActivo(producto);
    if (detalleVenta.getCantidad()<=0){
        throw new IllegalArgumentException("Cantidad invalida");
    }
    detalleVenta.setPrecioUnitario(producto.getPrecio());
    if(!detalleVentaDAO.insertarDetalleVenta(detalleVenta)){
        throw new RuntimeException("No se pudo insertar el detalle de venta");
    }
        return true;
    }

    private void validarId(int id, String entidad){
        if(id<=0){
            throw new IllegalArgumentException("Id "+entidad+" invalido");
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
