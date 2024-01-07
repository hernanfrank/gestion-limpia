package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialEstadoPedido;
import com.burbujas.gestionlimpia.models.entities.HistorialMaquinaPedido;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPedidoService {
    public List<Pedido> findAll();

    public Page<Pedido> findAll(Pageable pageable); // busca todos los clientes, con paginacion

    public List<HistorialEstadoPedido> getHistorialEstadosByPedidoId(Long id);

    public List<HistorialMaquinaPedido> getHistorialMaquinasByPedidoId(Long id);

    public Pedido findById(Long id);

    public void save(Pedido pedido);

    public void delete(Long id);

}
