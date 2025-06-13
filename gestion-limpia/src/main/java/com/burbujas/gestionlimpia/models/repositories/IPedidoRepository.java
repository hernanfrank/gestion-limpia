package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    Pedido findByMaquinaActualId(Long maquinaId);

    List<Pedido> findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();

    List<Pedido> findAllByEstadoActual(EstadoPedido estadoActual);

    List<Pedido> findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(EstadoPedido estadoActual);

    List<Pedido> findAllByTipo(TipoPedido tipo);

    @Query("SELECT DISTINCT CAST(CONCAT(YEAR(pedido.fechaHoraEntrega),'-', LPAD(CAST(MONTH(pedido.fechaHoraEntrega) AS STRING),2,'0'),'-01') AS DATE) FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL ORDER BY CAST(CONCAT(YEAR(pedido.fechaHoraEntrega),'-', LPAD(CAST(MONTH(pedido.fechaHoraEntrega) AS STRING),2,'0'),'-01') AS DATE) DESC")
    List<Date> findAllMonthsWithPedidosEntregados();

    @Query("SELECT COUNT(pedido.id) FROM Pedido pedido WHERE pedido.fechaHoraEntrega BETWEEN :fechaDesde AND :fechaHasta")
    Integer countAllByFechaAfterAndFechaBefore(@Param("fechaDesde") Timestamp fechaDesde, @Param("fechaHasta") Timestamp fechaHasta);

    @Query("SELECT AVG(TIMESTAMPDIFF(DAY, pedido.fechaHoraIngreso, pedido.fechaHoraEntrega)) FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL AND pedido.fechaHoraEntrega BETWEEN :fechaDesde AND :fechaHasta")
    Double avgTiempoPorPedidoByMes(@Param("fechaDesde") Timestamp fechaDesde, @Param("fechaHasta") Timestamp fechaHasta);

    @Query("SELECT pedido.cliente.nombreApellido as cliente, COUNT(pedido) as cantidad FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL GROUP BY cliente ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> countAllGroupByCliente();

    @Query("SELECT pedido.cliente.nombreApellido as cliente, SUM(pedido.precio) as ingresos FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL GROUP BY cliente ORDER BY ingresos DESC LIMIT 10")
    List<Object[]> sumAllGroupByCliente();

    @Query("SELECT DATE_FORMAT(pedido.fechaHoraEntrega, '%Y-%m-01') as mes, COUNT(pedido) as cantidad FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL AND DATE_FORMAT(pedido.fechaHoraEntrega, '%Y-%m-01') <= DATE_FORMAT(:fechaBusqueda, '%Y-%m-01') AND TIMESTAMPDIFF(MONTH, :fechaBusqueda, pedido.fechaHoraEntrega) <= 6 GROUP BY mes ORDER BY mes ASC")
    List<Object[]> countAllGroupByMes(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query(value = "CALL AVG_TIEMPO_BY_ESTADO_PEDIDO();", nativeQuery = true)
    List<Object[]> avgTiempoByEstadoPedido();

    @Query(value = "CALL AVG_TIEMPO_BY_TIPO_PEDIDO();", nativeQuery = true)
    List<Object[]> avgTiempoByTipoPedido();

    @Query("SELECT DAYOFWEEK(pedido.fechaHoraEntrega) AS dia, COUNT(pedido) AS cantidad FROM Pedido pedido WHERE pedido.fechaHoraEntrega IS NOT NULL AND MONTH(pedido.fechaHoraEntrega) = MONTH(:fechaBusqueda) AND YEAR(pedido.fechaHoraEntrega) = YEAR(:fechaBusqueda) GROUP BY dia ORDER BY dia ASC")
    List<Object[]> countAllGroupByDiaDeSemana(@Param("fechaBusqueda") Timestamp fechaBusqueda);

}
