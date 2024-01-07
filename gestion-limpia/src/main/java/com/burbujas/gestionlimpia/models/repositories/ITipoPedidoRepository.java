package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoPedidoRepository extends JpaRepository<TipoPedido, Long> {
}
