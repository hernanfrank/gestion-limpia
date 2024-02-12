package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.HistorialProductoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHistorialProductoPedido extends JpaRepository<HistorialProductoPedido, Long> {
}
