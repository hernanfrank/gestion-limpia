package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;

import java.util.List;

public interface ICajaService {
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc();

    public MovimientoCaja findMovimientoCajaById(Long id);

    public void saveMovimientoCaja(MovimientoCaja movimientoCaja);

    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja);
}
