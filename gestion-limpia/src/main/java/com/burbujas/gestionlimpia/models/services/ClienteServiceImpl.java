package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service("ClienteServiceImpl")
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
    public List<Cliente> findAllEliminados() {
        return this.clienteRepository.findAllEliminados();
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
    public Integer countAllByMes(String fecha) {
        LocalDate fechaDesde = LocalDate.parse(fecha).withDayOfMonth(1);
        LocalDate fechaHasta = fechaDesde.with(lastDayOfMonth());
        Integer val = this.clienteRepository.countAllByFechaAfterAndFechaBefore(Timestamp.valueOf(fechaDesde.atStartOfDay()), Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));
        return val == null ? 0 : val;//si es null, no hubo pedidos entregados entre las fechas
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Long id) {
        this.clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void restaurarCliente(Long id) {
        this.clienteRepository.restaurarCliente(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente fetchByIdWithPedidos(Long id) {
        return this.clienteRepository.fetchByIdWithPedidos(id);
    }
}
