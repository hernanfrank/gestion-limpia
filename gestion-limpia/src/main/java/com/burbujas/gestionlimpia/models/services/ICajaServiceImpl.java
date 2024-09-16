package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Gasto;
import com.burbujas.gestionlimpia.models.repositories.IGastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("CajaServiceImpl")
public class ICajaServiceImpl implements ICajaService{

    private final IGastoRepository gastoRepository;

    @Autowired
    public ICajaServiceImpl(IGastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> findAllGastosOrderByFechaDesc() {
        return this.gastoRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public Gasto findGastoById(Long id) {
        return this.gastoRepository.findById(id).orElse(null);
    }

    @Override
    public void saveGasto(Gasto gasto) {
        this.gastoRepository.save(gasto);
    }

    @Override
    public void deleteGasto(Gasto gasto) {
        this.gastoRepository.delete(gasto);
    }
}
