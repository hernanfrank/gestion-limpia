package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
}
