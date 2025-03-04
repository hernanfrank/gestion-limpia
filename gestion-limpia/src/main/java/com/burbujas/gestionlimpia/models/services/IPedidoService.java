package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialEstadoPedido;
import com.burbujas.gestionlimpia.models.entities.HistorialMaquinaPedido;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;

import java.util.List;

public interface IPedidoService {
    public List<Pedido> findAll();

    public List<Pedido> findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();

    public List<Pedido> findAllByEstadoActual(EstadoPedido estadoActual);

    public List<Pedido> findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(EstadoPedido estadoActual);

    public List<Pedido> findAllByTipoPedido(TipoPedido tipoPedido);

    public List<HistorialEstadoPedido> getHistorialEstadosByPedidoId(Long id);

    public List<HistorialMaquinaPedido> getHistorialMaquinasByPedidoId(Long id);

    public String asignarAMaquina(Long pedidoId, Integer maquinaNumero, boolean force);

    public boolean asignarAMaquina(Long pedidoId, Integer maquinaNumero, String estadoId);

    public Pedido findById(Long id);

    public Pedido findByMaquinaActualId(Long id);

    public void save(Pedido pedido) throws Exception;

    public void deleteById(Long id);

}
