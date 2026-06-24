package com.proyectoProducto.service;

import com.proyectoProducto.dao.UsuarioDAO;
import com.proyectoProducto.dao.VentaDAO;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.model.Venta;
import com.proyectoProducto.util.ValidarUsuario;

import java.math.BigDecimal;
import java.util.List;

public class VentaService {
    private final VentaDAO ventaDAO;
    private final UsuarioDAO usuarioDAO;

    public VentaService(VentaDAO ventaDAO, UsuarioDAO usuarioDAO) {
        this.ventaDAO = ventaDAO;
        this.usuarioDAO = usuarioDAO;
    }
    public List<Venta> listarVentas(){
        return ventaDAO.listarVentas();
    }
    public List<Venta> listarVentasActivas(){
        return ventaDAO.listarVentasActivas();
    }
    public Venta buscarPorId(int id){
        validarId(id);
        return ventaDAO.buscarVentaPorId(id).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }
    public List<Venta> buscarVentasPorCliente(int idCliente){
        validarId(idCliente);
        return ventaDAO.buscarVentasPorCliente(idCliente);
    }
    public List<Venta> buscarVentasPorVendedor(int idVendedor){
        validarId(idVendedor);
        usuarioDAO.buscarPorId(idVendedor).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ventaDAO.buscarVentasPorUsuario(idVendedor);
    }

    public boolean crearVenta(Usuario vendedor, Venta venta){
        ValidarUsuario.validarUsuarioActivo(vendedor);
        validarVenta(venta);
        venta.setIdUsuario(vendedor.getIdUsuario());
        boolean insertado =ventaDAO.insertar(venta);
        if(!insertado){
            throw new RuntimeException("Error al registrar la venta");
        }
        return true;
    }
    public boolean cambiarActivo(Usuario admin,int idVenta, boolean activo){
        ValidarUsuario.validarAdmin(admin);
        validarId(idVenta);
        ventaDAO.buscarVentaPorId(idVenta).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        boolean actualizado=ventaDAO.cambiarActivo(idVenta,activo);
        if(!actualizado){
            throw new RuntimeException("Error al actualizar el activo");
        }
        return true;
    }
    private void validarVenta(Venta venta){
        if(venta==null){
            throw new IllegalArgumentException("Venta invalida");
        }
        if(venta.getTotal()==null){
            throw new IllegalArgumentException("El total es obligatorio");
        }
        if(venta.getTotal().compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("El total debe ser mayor que 0");
        }

        if (venta.getIdCliente()<=0){
            throw new IllegalArgumentException("ID de cliente invalido");
        }
    }
    private void validarId(int id){
        if(id<=0){
            throw new IllegalArgumentException("ID invalido");
        }
    }
}
