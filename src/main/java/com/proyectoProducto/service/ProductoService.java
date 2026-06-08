package com.proyectoProducto.service;

import com.proyectoProducto.dao.ProductoDAO;
import com.proyectoProducto.model.Producto;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.util.ValidarUsuario;

import java.util.List;

public class ProductoService {
    private final ProductoDAO productoDAO;
    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listarProductos(){
        return productoDAO.listarProductos();
    }
    public List<Producto> listarProductosActivo(){
        return productoDAO.listarProdctosActivo();
    }

    public Producto buscarPorId(int id){
      validarIdProducto(id);
        return productoDAO.buscarPorId(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    public Producto buscarPorNombre(String nombre){
        if(nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return productoDAO.buscarPorNombre(nombre).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto crearProducto(Usuario admin, Producto productoNuevo){
        ValidarUsuario.validarAdmin(admin);
        validarProducto(productoNuevo);
        productoNuevo.setActivo(true);

        if(!productoDAO.insertar(productoNuevo)){
            throw new RuntimeException("Error al insertar el producto");
        }
        return productoNuevo;
    }
    public boolean actualizarProducto(Usuario admin,Producto producto){
        ValidarUsuario.validarAdmin(admin);
        validarIdProducto(producto.getIdProducto());
        validarProducto(producto);
        productoDAO.buscarPorId(producto.getIdProducto()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        boolean actualizado=productoDAO.actualizar(producto);
        if(!actualizado){
            throw new RuntimeException("Error al actualizar el producto");
        }
        return true;
    }
    public boolean actualizarActivo(Usuario admin,int idProducto,boolean activo){
      ValidarUsuario.validarAdmin(admin);
      validarIdProducto(idProducto);
      productoDAO.buscarPorId(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
      boolean actualizado=productoDAO.cambiarActivo(idProducto,activo);
      if(!actualizado){
          throw new RuntimeException("Error al actualizar el activo");
      }
      return true;
    }
    public boolean agregarStock(Usuario admin,int idProducto,int cantidad){
        ValidarUsuario.validarAdmin(admin);
        validarIdProducto(idProducto);
        validarCantidad(cantidad);
        boolean actualizado=productoDAO.agregarStock(idProducto,cantidad);
        if(!actualizado){
            throw new RuntimeException("Producto no encontrado");
        }
        return true;
    }
    public boolean descontarStockPorVenta(int idProducto, int cantidad){
        validarIdProducto(idProducto);
        Producto producto=productoDAO.buscarPorId(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        validarCantidad(cantidad);
        if(producto.getStock()<cantidad){
            throw new RuntimeException("Stock insuficiente");
        }
        boolean actualizado=productoDAO.descontarStock(idProducto,cantidad);
        if(!actualizado){
            throw new RuntimeException("Error al descontar el stock");
        }
        return true;
    }
    public boolean actualizarStock(Usuario admin,int idProducto,int nuevoStock){
    ValidarUsuario.validarAdmin(admin);
    validarIdProducto(idProducto);
    validarStock(nuevoStock);
    boolean actualizado=productoDAO.actualizarStock(idProducto,nuevoStock);
    if(!actualizado){
        throw new RuntimeException("Producto no encontrado");
    }
    return true;
    }


     private void validarProducto(Producto producto){
         if(producto==null){
             throw new IllegalArgumentException("Producto invalido");
         }
         if(producto.getNombre()==null || producto.getNombre().isBlank()){
             throw new IllegalArgumentException("El nombre es obligatorio");
         }
         if(producto.getDescripcion()==null || producto.getDescripcion().isBlank()){
             throw new IllegalArgumentException("La descripcion es obligatoria");
         }
         if(producto.getPrecio()<=0){
             throw new IllegalArgumentException("El precio es invalido");
         }
         if(producto.getStock()<0){
             throw new IllegalArgumentException("El stock es invalido");
         }
         if(producto.getIdCategoria()<=0){
             throw new IllegalArgumentException("La categoria es invalida");
         }                                                  
     }
     private void validarIdProducto(int idProducto){
         if(idProducto<=0){
             throw new IllegalArgumentException("Id invalido");
         }
     }
     private void validarCantidad(int cantidad){
         if(cantidad<=0){
             throw new IllegalArgumentException("Cantidad invalida");
         }
     }
     private void validarStock(int stock){
        if(stock<0){
            throw new IllegalArgumentException("Stock invalido");
        }
     }



}
