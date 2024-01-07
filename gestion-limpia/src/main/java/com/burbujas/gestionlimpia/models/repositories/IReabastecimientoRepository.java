package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReabastecimientoRepository extends JpaRepository<Reabastecimiento, Long> {
}
