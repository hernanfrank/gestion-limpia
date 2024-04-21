package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import com.burbujas.gestionlimpia.models.repositories.IMaquinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class MaquinaServiceImpl implements IMaquinaService{

    private final IMaquinaRepository maquinaRepository;

    @Autowired
    public MaquinaServiceImpl(IMaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    @Override
    public List<Maquina> findAll() {
        return this.maquinaRepository.findAll();
    }

    @Override
    public Optional<Maquina> findById(Long id) {
        return this.maquinaRepository.findById(id);
    }

    @Override
    public Optional<Maquina> findByTipo(TipoMaquina tipo) {
        return Optional.ofNullable(this.maquinaRepository.findByTipo(tipo));
    }

    @Override
    public void save(Maquina maquina) {
        this.maquinaRepository.save(maquina);
    }

    @Override
    public void delete(Long id) {
        this.maquinaRepository.deleteById(id);
    }
}
