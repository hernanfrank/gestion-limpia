package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;

import java.util.Date;
import java.util.List;

public interface ICajaService {
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc();

    public List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta);

    public MovimientoCaja findMovimientoCajaById(Long id);

    public List<MovimientoCaja> findAllMovimientosByTipoCaja(TipoCaja tipoCaja);

    public MovimientoCaja generarMovimientoPorCobranzaPedido(Pedido pedido, TipoCaja tipoCaja);

    public void saveMovimientoCaja(MovimientoCaja movimientoCaja);

    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja);

    public MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);
}
