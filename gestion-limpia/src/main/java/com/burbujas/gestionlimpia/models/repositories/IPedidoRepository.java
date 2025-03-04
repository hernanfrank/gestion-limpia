package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    Pedido findByMaquinaActualId(Long maquinaId);

    List<Pedido> findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();

    List<Pedido> findAllByEstadoActual(EstadoPedido estadoActual);

    List<Pedido> findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(EstadoPedido estadoActual);

    List<Pedido> findAllByTipo(TipoPedido tipo);

}
