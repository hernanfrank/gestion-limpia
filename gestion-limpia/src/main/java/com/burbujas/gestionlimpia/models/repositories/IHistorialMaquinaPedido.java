package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.HistorialMaquinaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHistorialMaquinaPedido extends JpaRepository<HistorialMaquinaPedido, Long> {
}
