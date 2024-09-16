package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findAllByOrderByFechaDesc();
}
