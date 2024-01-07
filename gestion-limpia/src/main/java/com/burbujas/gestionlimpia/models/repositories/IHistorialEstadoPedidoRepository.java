package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.HistorialEstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHistorialEstadoPedidoRepository extends JpaRepository<HistorialEstadoPedido, Long> {
}
