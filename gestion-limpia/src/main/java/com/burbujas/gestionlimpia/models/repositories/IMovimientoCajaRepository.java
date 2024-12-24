package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findAllByOrderByFechaDesc();

    List<MovimientoCaja> findAllByTipoCaja(TipoCaja tipoCaja);
    MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);
}
