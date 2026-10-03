package com.proyectoProducto.service;

import com.proyectoProducto.dao.ProductoDAO;
import com.proyectoProducto.model.Producto;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.util.ValidarUsuario;

import java.math.BigDecimal;
import java.sql.Connection;
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
        return productoDAO.listarProdctosActivos();
    }

    public Producto buscarProductoPorId(int id){
      validarIdProducto(id);
        return productoDAO.buscarProductoPorId(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    public Producto buscarProductoPorNombre(String nombre){
        if(nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        return productoDAO.buscarProductoPorNombre(nombre).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto ingresarProducto(Usuario admin, Producto productoNuevo){
        ValidarUsuario.validarAdmin(admin);
        validarProducto(productoNuevo);
        productoNuevo.setActivo(true);


        if(!productoDAO.insertarProducto(productoNuevo)){
            throw new RuntimeException("Error al insertar el producto");
        }
        return productoNuevo;
    }
    public boolean actualizarProducto(Usuario admin,Producto producto){
        ValidarUsuario.validarAdmin(admin);
        validarIdProducto(producto.getIdProducto());
        validarProducto(producto);
        productoDAO.buscarProductoPorId(producto.getIdProducto()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        boolean actualizado=productoDAO.actualizarProducto(producto);
        if(!actualizado){
            throw new RuntimeException("Error al actualizar el producto");
        }
        return true;
    }
    public boolean actualizarActivoProducto(Usuario admin,int idProducto,boolean activo){
      ValidarUsuario.validarAdmin(admin);
      validarIdProducto(idProducto);
      productoDAO.buscarProductoPorId(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
      boolean actualizado=productoDAO.actualizarActivoProducto(idProducto,activo);
      if(!actualizado){
          throw new RuntimeException("Error al actualizar el activo");
      }
      return true;
    }
    public boolean agregarStockProducto(Usuario admin,int idProducto,int cantidad){
        ValidarUsuario.validarAdmin(admin);
        validarIdProducto(idProducto);
        validarCantidadProducto(cantidad);
        boolean actualizado=productoDAO.agregarStockProducto(idProducto,cantidad);
        if(!actualizado){
            throw new RuntimeException("Producto no encontrado");
        }
        return true;
    }
    public boolean descontarStockProducto(int idProducto, int cantidad, Connection conn){
        validarStockDisponibleProducto(idProducto,cantidad);
        boolean actualizado=productoDAO.descontarStockProducto(idProducto,cantidad,conn);
        if(!actualizado){
            throw new RuntimeException("Error al descontar el stock");
        }
        return true;
    }
    public boolean actualizarStockProducto(Usuario admin,int idProducto,int nuevoStock){
    ValidarUsuario.validarAdmin(admin);
    validarIdProducto(idProducto);
    validarStockProducto(nuevoStock);
    boolean actualizado=productoDAO.actualizarStockProducto(idProducto,nuevoStock);
    if(!actualizado){
        throw new RuntimeException("Producto no encontrado");
    }
    return true;
    }
    public void validarStockDisponibleProducto(int idProducto, int cantidad){
        validarIdProducto(idProducto);
        validarCantidadProducto(cantidad);

        Producto producto=productoDAO.buscarProductoPorId(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if(producto.getStock()<cantidad){
            throw new RuntimeException(producto.getNombre()+" no cuenta con el stock suficiente");
        }

    }


     private void validarProducto(Producto producto){
         if(producto==null){
             throw new IllegalArgumentException("Producto inválido");
         }
         if(producto.getNombre()==null || producto.getNombre().isBlank()){
             throw new IllegalArgumentException("El nombre es obligatorio");
         }
         if(producto.getDescripcion()==null || producto.getDescripcion().isBlank()){
             throw new IllegalArgumentException("La descripción es obligatoria");
         }

         if(producto.getPrecio().compareTo(BigDecimal.ZERO)<=0){
             throw new IllegalArgumentException("El precio es inválido");
         }
         if(producto.getStock()<0){
             throw new IllegalArgumentException("El stock es inválido");
         }
         if(producto.getIdCategoria()<=0){
             throw new IllegalArgumentException("La categoria es inválida");
         }                                                  
     }
     private void validarIdProducto(int idProducto){
         if(idProducto<=0){
             throw new IllegalArgumentException("Id inválido");
         }
     }
     private void validarCantidadProducto(int cantidad){
         if(cantidad<=0){
             throw new IllegalArgumentException("Cantidad inválida");
         }
     }
     private void validarStockProducto(int stock){
        if(stock<0){
            throw new IllegalArgumentException("Stock inválido");
        }
     }
    


}
