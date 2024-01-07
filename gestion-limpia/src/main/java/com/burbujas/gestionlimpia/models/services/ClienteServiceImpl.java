package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ClienteService")
public class ClienteServiceImpl implements IClienteService{

    private final IClienteRepository clienteRepository;
    
    @Autowired
    public ClienteServiceImpl(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return this.clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)// los métodos que no modifiquen el registro en la bdd deben ponerse como readOnly = true
    public Page<Cliente> findAll(Pageable pageable) {
        return this.clienteRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Cliente cliente) {
        this.clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findById(Long id) {
        return this.clienteRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findByNombreApellido(String nombreApellido) {
        return this.clienteRepository.findByNombreApellidoLike("%"+nombreApellido+"%");
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Long id) {
        this.clienteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente fetchByIdWithPedidos(Long id) {
        return this.clienteRepository.fetchByIdWithPedidos(id);
    }
}
