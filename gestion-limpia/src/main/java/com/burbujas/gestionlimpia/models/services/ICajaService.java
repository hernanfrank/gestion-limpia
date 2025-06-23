package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICajaService {
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc();

    public List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta);

    public Double sumAllByMes(String fecha);

    public List<Object[]> sumAllGroupByMes(String fecha);

    public List<Object[]> sumAllGroupByDia(String fecha);

    public List<Object[]> sumAllGroupByTipoCaja(String fecha);

    public List<Object[]> sumAllGroupByTipoPedido(String fecha);

    public MovimientoCaja findMovimientoCajaById(Long id);

    public List<MovimientoCaja> findAllMovimientosByTipoCaja(TipoCaja tipoCaja);

    public List<MovimientoCaja> findAllMovimientosCajaEliminados();

    public MovimientoCaja generarMovimientoPorCobranzaPedido(Pedido pedido, TipoCaja tipoCaja);

    public void saveMovimientoCaja(MovimientoCaja movimientoCaja);

    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja);

    public void restaurarMovimientoCaja(Long id);

    public MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);

    public File exportarMovimientosCaja(Date fechaDesde, Date fechaHasta, String nombreArchivo) throws Exception;
}
