package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface IMovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findAllByOrderByFechaDesc();

    List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta);

    // usamos una nativeQuery para que ignore la restricción de eliminado <> true
    @Query(value = "SELECT * FROM movimientos_caja mc WHERE mc.eliminado = TRUE ORDER BY mc.id DESC", nativeQuery = true)
    List<MovimientoCaja> findAllMovimientosCajaEliminados();

    @Query("SELECT SUM(movimiento.monto) FROM MovimientoCaja movimiento WHERE movimiento.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double sumAllByFechaAfterAndFechaBefore(@Param("fechaDesde") Timestamp fechaDesde, @Param("fechaHasta") Timestamp fechaHasta);

    @Query("SELECT DISTINCT DATE_FORMAT(movimiento.fecha, '%Y-%m-01') as mes, TRUNCATE(SUM(movimiento.monto),2) as total FROM MovimientoCaja movimiento WHERE DATE_FORMAT(movimiento.fecha, '%Y-%m-01') <= DATE_FORMAT(:fechaBusqueda, '%Y-%m-01') AND TIMESTAMPDIFF(MONTH, :fechaBusqueda, movimiento.fecha) <= 6 GROUP BY mes ORDER BY mes ASC")
    List<Object[]> sumAllGroupByMes(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query("SELECT DISTINCT DATE_FORMAT(movimiento.fecha, '%Y-%m-%d') as dia, TRUNCATE(SUM(movimiento.monto),2) as total FROM MovimientoCaja movimiento WHERE YEAR(movimiento.fecha) = YEAR(:fechaBusqueda) AND MONTH(movimiento.fecha) = MONTH(:fechaBusqueda) GROUP BY dia ORDER BY dia ASC")
    List<Object[]> sumAllGroupByDia(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query("SELECT DISTINCT movimiento.tipoCaja as tipoCaja, TRUNCATE(SUM(movimiento.monto),2) as total FROM MovimientoCaja movimiento WHERE YEAR(movimiento.fecha) = YEAR(:fechaBusqueda) AND MONTH(movimiento.fecha) = MONTH(:fechaBusqueda) GROUP BY tipoCaja")
    List<Object[]> sumAllGroupByTipoCaja(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query("SELECT DISTINCT movimiento.pedido.tipo.descripcion as tipoPedido, TRUNCATE(SUM(movimiento.monto),2) as total FROM MovimientoCaja movimiento WHERE YEAR(movimiento.fecha) = YEAR(:fechaBusqueda) AND MONTH(movimiento.fecha) = MONTH(:fechaBusqueda) GROUP BY tipoPedido ORDER BY total DESC")
    List<Object[]> sumAllGroupByTipoPedido(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    List<MovimientoCaja> findAllByTipoCaja(TipoCaja tipoCaja);

    MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento);

    @Modifying
    @Transactional
    @Query(value ="""
            UPDATE movimientos_caja mc
            SET mc.eliminado = false
            WHERE mc.pedido_id = :id
       """, nativeQuery = true)
    void restaurarMovimientoCaja(@Param("id") Long id);
}
