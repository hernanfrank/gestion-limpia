package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;

import java.util.List;

public interface ICajaService {
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc();

    public MovimientoCaja findMovimientoCajaById(Long id);

    public List<MovimientoCaja> findAllMovimientosByTipoCaja(TipoCaja tipoCaja);
    public void saveMovimientoCaja(MovimientoCaja movimientoCaja);

    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja);

    public MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);
}
