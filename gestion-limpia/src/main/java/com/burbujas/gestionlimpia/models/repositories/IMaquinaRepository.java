package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMaquinaRepository extends JpaRepository<Maquina, Long> {
}
