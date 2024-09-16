package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Cliente;

import java.util.List;

public interface IClienteService {
    public List<Cliente> findAll();

    public void save(Cliente cliente);

    public Cliente findById(Long id);

    public List<Cliente> findByNombreApellido(String nombreApellido);

    public void delete(Long id);

    public Cliente fetchByIdWithPedidos(Long id);
}
