package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialEstadoPedido;
import com.burbujas.gestionlimpia.models.entities.HistorialMaquinaPedido;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.repositories.IPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PedidoServiceImpl implements IPedidoService{

    private final IPedidoRepository pedidoRepository;

    @Autowired
    public PedidoServiceImpl(IPedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> findAll() {
        return this.pedidoRepository.findAll();
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        return this.pedidoRepository.findAll(pageable);
    }

    @Override
    public List<HistorialEstadoPedido> getHistorialEstadosByPedidoId(Long id) {
        // si no existe el pedido retorno null
        try {
            return Objects.requireNonNull(this.pedidoRepository.findById(id).orElse(null)).getHistorialEstadoPedido();
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public List<HistorialMaquinaPedido> getHistorialMaquinasByPedidoId(Long id) {
        // si no existe el pedido retorno null
        try {
            return Objects.requireNonNull(this.pedidoRepository.findById(id).orElse(null)).getHistorialMaquinaPedido();
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public Pedido findById(Long id) {
        return this.pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Pedido pedido) {
        this.pedidoRepository.save(pedido);
    }

    @Override
    public void delete(Long id) {
        this.pedidoRepository.deleteById(id);
    }
}
