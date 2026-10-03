package com.proyectoProducto.service;


import com.proyectoProducto.dao.VentaDAO;
import com.proyectoProducto.db.ConexionDB;
import com.proyectoProducto.model.*;
import com.proyectoProducto.util.ValidarUsuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VentaService {
    private final VentaDAO ventaDAO;
    private final UsuarioService usuarioService;
    private final DetalleVentaService detalleVentaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private static final String ENTIDAD_VENTA="venta";
    private static final String ENTIDAD_VENDEDOR="vendedor";
    private static final String ENTIDAD_CLIENTE="cliente";


    public VentaService(VentaDAO ventaDAO, UsuarioService usuarioService,ProductoService productoService,DetalleVentaService detalleVentaService, ClienteService clienteService ) {
        this.ventaDAO = ventaDAO;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.detalleVentaService = detalleVentaService;
        this.clienteService = clienteService;

    }
    public List<Venta> listarVentas(){
        return ventaDAO.listarVentas();
    }
    public List<Venta> listarVentasActivas(){
        return ventaDAO.listarVentasActivas();
    }
    public Venta buscarVentaPorId(int id){
        validarIdVenta(id,ENTIDAD_VENTA);
        return ventaDAO.buscarVentaPorId(id).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }
    public List<Venta> buscarVentasPorCliente(int idCliente){
        validarIdVenta(idCliente,ENTIDAD_CLIENTE);
        return ventaDAO.buscarVentasPorCliente(idCliente);
    }
    public List<Venta> buscarVentasPorVendedor(int idVendedor){
        validarIdVenta(idVendedor,ENTIDAD_VENDEDOR);
        usuarioService.buscarUsuarioPorId(idVendedor);
        return ventaDAO.buscarVentasPorUsuario(idVendedor);
    }

    public Venta ingresarVenta(Usuario vendedor, Venta venta, List<DetalleVenta> detallesVenta) {
        ValidarUsuario.validarUsuarioActivo(vendedor);
        validarVenta(venta);
        validarCliente(venta.getIdCliente());

        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle");
        }
        for (DetalleVenta detalle : detallesVenta) {
            productoService.validarStockDisponibleProducto(detalle.getIdProducto(), detalle.getCantidad());
        }
        venta.setIdUsuario(vendedor.getIdUsuario());
        venta.setTotal(calcularTotalVenta(detallesVenta));
        Connection conn = null;

        try {
            conn=ConexionDB.getConection();
            conn.setAutoCommit(false);

            Venta ventaCreada = ventaDAO.insertarVenta(venta, conn);

            for (DetalleVenta detalle : detallesVenta) {
                detalle.setIdVenta(ventaCreada.getIdVenta());
                detalleVentaService.ingresarDetalleVenta(detalle, conn);
            }
            for (DetalleVenta detalle : detallesVenta) {
                productoService.descontarStockProducto(detalle.getIdProducto(), detalle.getCantidad(), conn);
            }

            conn.commit();

            return ventaCreada;
        } catch (Exception e) {
            if(conn!=null){
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error al hacer rollback", ex);
                }
            }
            throw new RuntimeException("Error al crear la venta",e);
        } finally {
            if (conn != null) {
                try{
                    conn.close();
                }catch(SQLException e){
                    throw new RuntimeException("Error al cerrar conexion", e);
                }
            }
        }
    }

    public boolean cambiarActivoVenta(Usuario admin,int idVenta, boolean activo){
        ValidarUsuario.validarAdmin(admin);
        validarIdVenta(idVenta,ENTIDAD_VENTA);
        ventaDAO.buscarVentaPorId(idVenta).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        if(!ventaDAO.cambiarActivoVenta(idVenta,activo)){
            throw new RuntimeException("Error al actualizar el activo");
        }
        return true;
    }
    private void validarVenta(Venta venta){
        if(venta==null){
            throw new IllegalArgumentException("Venta inválida");
        }

    }
    private void validarIdVenta(int id, String entidad){
        if(id<=0){
            throw new IllegalArgumentException("Id "+entidad+" inválido");
        }
    }
    private void validarCliente(int idCliente){
        validarIdVenta(idCliente,ENTIDAD_CLIENTE);
        Cliente cliente =clienteService.buscarClientePorId(idCliente);

        if(!cliente.getActivo()){
            throw new RuntimeException("Cliente inactivo");
        }

    }
    private BigDecimal calcularTotalVenta(List<DetalleVenta> detallesVenta){
        BigDecimal total=BigDecimal.ZERO;
        for(DetalleVenta detalle : detallesVenta){
           Producto producto= productoService.buscarProductoPorId(detalle.getIdProducto());

           BigDecimal subtotal= producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            total=total.add(subtotal);
        }
        return total;

    }
}
