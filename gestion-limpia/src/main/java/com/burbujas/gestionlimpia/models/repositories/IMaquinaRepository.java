package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMaquinaRepository extends JpaRepository<Maquina, Long> {
    List<Maquina> findAllByTipo(TipoMaquina tipo);

    Optional<Maquina> findByNumero(Integer maquinaNumero);
}
