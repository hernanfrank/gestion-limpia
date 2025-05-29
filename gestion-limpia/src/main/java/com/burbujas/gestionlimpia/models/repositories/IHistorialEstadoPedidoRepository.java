package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.HistorialEstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHistorialEstadoPedidoRepository extends JpaRepository<HistorialEstadoPedido, Long> {
}
