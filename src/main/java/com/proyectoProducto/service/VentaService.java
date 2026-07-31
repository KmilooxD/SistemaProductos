package com.proyectoProducto.service;


import com.proyectoProducto.dao.VentaDAO;
import com.proyectoProducto.model.*;
import com.proyectoProducto.util.ValidarUsuario;

import java.math.BigDecimal;
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
    public Venta buscarPorId(int id){
        validarId(id,ENTIDAD_VENTA);
        return ventaDAO.buscarVentaPorId(id).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }
    public List<Venta> buscarVentasPorCliente(int idCliente){
        validarId(idCliente,ENTIDAD_CLIENTE);
        return ventaDAO.buscarVentasPorCliente(idCliente);
    }
    public List<Venta> buscarVentasPorVendedor(int idVendedor){
        validarId(idVendedor,ENTIDAD_VENDEDOR);
        usuarioService.buscarPorId(idVendedor);
        return ventaDAO.buscarVentasPorUsuario(idVendedor);
    }

    public Venta crearVenta(Usuario vendedor, Venta venta, List<DetalleVenta> detallesVenta){
        ValidarUsuario.validarUsuarioActivo(vendedor);
        validarVenta(venta);
        validarCliente(venta.getIdCliente());

        if(detallesVenta==null || detallesVenta.isEmpty()){
            throw new IllegalArgumentException("La venta debe contener al menos un detalle");
        }
        for(DetalleVenta detalle : detallesVenta){
            productoService.validarStockDisponible(detalle.getIdProducto(),detalle.getCantidad());
        }

        venta.setIdUsuario(vendedor.getIdUsuario());
        venta.setTotal(calcularTotal(detallesVenta));
        Venta ventaCreada=ventaDAO.insertar(venta);

        for(DetalleVenta detalle : detallesVenta){
            detalle.setIdVenta(ventaCreada.getIdVenta());
            detalleVentaService.crearDetalleVenta(detalle);
        }
        for(DetalleVenta detalle : detallesVenta){
            productoService.descontarStock(detalle.getIdProducto(),detalle.getCantidad());
        }

        return ventaCreada;
    }


    public boolean cambiarActivo(Usuario admin,int idVenta, boolean activo){
        ValidarUsuario.validarAdmin(admin);
        validarId(idVenta,ENTIDAD_VENTA);
        ventaDAO.buscarVentaPorId(idVenta).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        if(!ventaDAO.cambiarActivo(idVenta,activo)){
            throw new RuntimeException("Error al actualizar el activo");
        }
        return true;
    }
    private void validarVenta(Venta venta){
        if(venta==null){
            throw new IllegalArgumentException("Venta invalida");
        }

    }
    private void validarId(int id, String entidad){
        if(id<=0){
            throw new IllegalArgumentException("Id "+entidad+" invalido");
        }
    }
    private void validarCliente(int idCliente){
        validarId(idCliente,ENTIDAD_CLIENTE);
        Cliente cliente =clienteService.buscarClientePorId(idCliente);

        if(!cliente.getActivo()){
            throw new RuntimeException("Cliente inactivo");
        }

    }
    private BigDecimal calcularTotal(List<DetalleVenta> detallesVenta){
        BigDecimal total=BigDecimal.ZERO;
        for(DetalleVenta detalle : detallesVenta){
           Producto producto= productoService.buscarPorId(detalle.getIdProducto());

           BigDecimal subtotal= producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            total=total.add(subtotal);
        }
        return total;

    }
}
