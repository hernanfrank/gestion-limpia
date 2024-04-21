package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;

import java.util.List;
import java.util.Optional;

public interface IMaquinaService {
    public List<Maquina> findAll();

    public void save(Maquina Maquina);

    public Optional<Maquina> findById(Long id);

    public Optional<Maquina> findByTipo(TipoMaquina tipo);

    public void delete(Long id);

}
