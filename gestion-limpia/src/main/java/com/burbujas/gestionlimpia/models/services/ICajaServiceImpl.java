package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.repositories.IMovimientoCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("CajaServiceImpl")
public class ICajaServiceImpl implements ICajaService{

    private final IMovimientoCajaRepository MovimientoCajaRepository;

    @Autowired
    public ICajaServiceImpl(IMovimientoCajaRepository MovimientoCajaRepository) {
        this.MovimientoCajaRepository = MovimientoCajaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc() {
        return this.MovimientoCajaRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public MovimientoCaja findMovimientoCajaById(Long id) {
        return this.MovimientoCajaRepository.findById(id).orElse(null);
    }

    @Override
    public void saveMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.MovimientoCajaRepository.save(movimientoCaja);
    }

    @Override
    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.MovimientoCajaRepository.delete(movimientoCaja);
    }
}
