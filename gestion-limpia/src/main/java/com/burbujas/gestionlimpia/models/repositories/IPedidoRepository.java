package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    Pedido findByMaquinaActualId(Long maquinaId);

    List<Pedido> findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();

    @Query(value = """
        SELECT 
            p.id AS pedido_id,
            p.fecha_hora_ingreso AS pedido_fecha_hora_ingreso,
            p.fecha_hora_entrega AS pedido_fecha_hora_entrega,
            p.precio AS pedido_precio,
            p.prioridad AS pedido_prioridad,
            p.estado_actual AS pedido_estado_actual,
            p.descripcion AS pedido_descripcion,
            c.nombre_apellido AS cliente_nombre_apellido,
            c.eliminado AS cliente_eliminado,
            tp.descripcion AS tipo_pedido_descripcion
        FROM pedidos p 
        JOIN tipos_pedido tp ON p.tipo_id = tp.id
        LEFT JOIN clientes c ON p.cliente_id = c.id
        WHERE p.eliminado = TRUE
        ORDER BY pedido_id DESC
    """, nativeQuery = true)
    List<Object[]> findAllEliminados();

    @Query(value = """
        SELECT
            c.eliminado
        FROM clientes c
        JOIN pedidos p ON p.cliente_id = c.id
        WHERE p.id = :id
    """, nativeQuery = true)
    Object[] checkClienteIsEliminadoByPedidoId(@Param("id") Long id);

    @Modifying
    @Transactional // vuelve a poner eliminado = false el pedido y los registros que referencian a este pedido en otras tablas
    @Query(value ="""
            UPDATE pedidos p,
                   historial_estados_pedidos hep,
                   historial_maquinas_pedidos hmp,
                   historial_productos_pedidos hpp,
                   movimientos_caja mc
            SET p.eliminado = false,
                hep.eliminado = false,
                hmp.eliminado = false,
                hpp.eliminado = false,
                mc.eliminado = false
            WHERE p.id = :id
                AND hep.pedido_id = p.id
                AND hmp.pedido_id = p.id
                AND hpp.pedido_id = p.id
                AND mc.pedido_id = p.id
       """, nativeQuery = true)
    void restaurarPedido(@Param("id") Long id);

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
