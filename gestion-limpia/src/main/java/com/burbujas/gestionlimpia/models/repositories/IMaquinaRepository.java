package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMaquinaRepository extends JpaRepository<Maquina, Long> {
    Maquina findByTipo(TipoMaquina tipo);

    Optional<Maquina> findByNumero(Integer maquinaNumero);
}
