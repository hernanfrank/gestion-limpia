package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IMovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findAllByOrderByFechaDesc();

    List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta);

    List<MovimientoCaja> findAllByTipoCaja(TipoCaja tipoCaja);
    MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);
}
