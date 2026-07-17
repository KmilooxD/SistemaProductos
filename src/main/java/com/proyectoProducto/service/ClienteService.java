package com.proyectoProducto.service;

import com.proyectoProducto.dao.ClienteDAO;
import com.proyectoProducto.model.Cliente;
import com.proyectoProducto.model.Usuario;
import com.proyectoProducto.util.*;

import java.util.List;

public class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
    public List<Cliente> listarClientes(){
        return clienteDAO.listarClientes();
    }
    public List<Cliente> listarClientesActivos(){
        return clienteDAO.listarClientesActivos();
    }
    public Cliente buscarClientePorId(int id){
       validarId(id);
        return clienteDAO.buscarClientePorId(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    public Cliente buscarClientePorRut(String rut){
        rut =ValidarRut.validarYFormatearRut(rut);
        return clienteDAO.buscarClientePorRut(rut).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    public Cliente buscarClientePorEmail(String email){
        email=ValidarEmail.validarYFormatearEmail(email);
        return clienteDAO.buscarClientePorEmail(email).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    public Cliente crearCliente(Usuario usuario, Cliente nuevoCliente){
        ValidarUsuario.validarUsuarioActivo(usuario);
        formatearCampos(nuevoCliente);
        validarCliente(nuevoCliente);
        if(clienteDAO.buscarClientePorRut(nuevoCliente.getRut()).isPresent()){
            throw new IllegalStateException("El rut ya se encuentra registrado");
        }
        if(clienteDAO.buscarClientePorEmail(nuevoCliente.getEmail()).isPresent()){
            throw new IllegalStateException("El email ya se encuentra registrado");
        }
        nuevoCliente.setActivo(true);
        if(!clienteDAO.insertarCliente(nuevoCliente)){
            throw  new RuntimeException("No se pudo crear el cliente");
        }
        return nuevoCliente;
    }
    public boolean actualizarCliente(Usuario usuario, Cliente cliente){
        ValidarUsuario.validarUsuarioActivo(usuario);
        validarId(cliente.getIdCliente());
        formatearCampos(cliente);
        validarCliente(cliente);
        clienteDAO.buscarClientePorId(cliente.getIdCliente()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Cliente clienteRut=clienteDAO.buscarClientePorRut(cliente.getRut()).orElse(null);
        if(clienteRut!=null && clienteRut.getIdCliente()!=cliente.getIdCliente()){
            throw new IllegalStateException("El rut ya se encuentra registrado");
        }
        Cliente clienteEmail=clienteDAO.buscarClientePorEmail(cliente.getEmail()).orElse(null);
        if(clienteEmail!=null && clienteEmail.getIdCliente()!=cliente.getIdCliente()){
            throw new IllegalStateException("El email ya se encuentra registrado");
        }

        if(!clienteDAO.actualizarCliente(cliente)){
            throw new RuntimeException("Error al actualizar el cliente");
        }
        return true;
    }
    public boolean actualizarActivo(Usuario admin,int id, boolean activo){
        ValidarUsuario.validarAdmin(admin);
        validarId(id);
        clienteDAO.buscarClientePorId(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        if(!clienteDAO.actualizarActivo(id,activo)){
            throw new RuntimeException("Error al actualizar el cliente");
        }
        return true;
    }
    private void validarCliente(Cliente cliente){
        if(cliente==null){
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if(cliente.getNombre()==null || cliente.getNombre().isBlank()){
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if(cliente.getRut()==null || cliente.getRut().isBlank()){
            throw new IllegalArgumentException("El rut es obligatorio");
        }
        if(cliente.getEmail()==null || cliente.getEmail().isBlank()){
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if(cliente.getTelefono()==null || cliente.getTelefono().isBlank()){
            throw new IllegalArgumentException("El telefono es obligatorio");
        }
        if(cliente.getRut().length()>12){
            throw new IllegalArgumentException("El rut ha superado el maximo de caracteres (12)");
        }
        if(cliente.getNombre().length()>50){
            throw new IllegalArgumentException("El nombre ha superado el maximo de caracteres (50)");
        }
        if(cliente.getEmail().length()>100){
            throw new IllegalArgumentException("El email ha superado el maximo de caracteres (100)");
        }
        if(cliente.getTelefono().length()>12){
            throw new IllegalArgumentException("El telefono ha superado el maximo de caracteres (12)");
        }
    }
    private void validarId(int id){
        if(id<=0){
            throw new IllegalArgumentException("Id invalido");
        }
    }
    private void formatearCampos(Cliente cliente){
        if(cliente==null){
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        cliente.setRut(ValidarRut.validarYFormatearRut(cliente.getRut()));
        cliente.setNombre(FormatearTexto.formatearNombre(cliente.getNombre()));
        cliente.setEmail(ValidarEmail.validarYFormatearEmail(cliente.getEmail()));
        cliente.setTelefono(ValidarTelefono.validarYFormatearTelefono(cliente.getTelefono()));
    }



}
