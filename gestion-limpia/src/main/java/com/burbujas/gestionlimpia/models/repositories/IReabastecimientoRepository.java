package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IReabastecimientoRepository extends JpaRepository<Reabastecimiento, Long> {
    @Query("SELECT SUM(reabastecimiento.precio) FROM Reabastecimiento reabastecimiento WHERE reabastecimiento.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double sumAllByFechaAfterAndFechaBefore(@Param("fechaDesde") Timestamp fechaDesde, @Param("fechaHasta") Timestamp fechaHasta);

    @Query("SELECT DISTINCT DATE_FORMAT(reabastecimiento.fecha, '%Y-%m-01') as mes, TRUNCATE(SUM(reabastecimiento.precio),2) as total FROM Reabastecimiento reabastecimiento WHERE DATE_FORMAT(reabastecimiento.fecha, '%Y-%m-01') <= DATE_FORMAT(:fechaBusqueda, '%Y-%m-01') AND TIMESTAMPDIFF(MONTH, :fechaBusqueda, reabastecimiento.fecha) <= 6 GROUP BY mes ORDER BY mes ASC")
    List<Object[]> sumAllReabastecimientosGroupByMes(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query("SELECT DISTINCT DATE_FORMAT(reabastecimiento.fecha, '%Y-%m-01') as mes, COUNT(reabastecimiento) as total FROM Reabastecimiento reabastecimiento WHERE DATE_FORMAT(reabastecimiento.fecha, '%Y-%m-01') <= DATE_FORMAT(:fechaBusqueda, '%Y-%m-01') AND TIMESTAMPDIFF(MONTH, :fechaBusqueda, reabastecimiento.fecha) <= 6 GROUP BY mes ORDER BY mes ASC")
    List<Object[]> countAllReabastecimientosGroupByMes(@Param("fechaBusqueda") Timestamp fechaBusqueda);

    @Query(value = """
        SELECT
               reabastecimiento.id,
               reabastecimiento.fecha,
               producto.tipo,
               reabastecimiento.cantidad_producto,
               reabastecimiento.precio,
               proveedor.nombre
        FROM reabastecimientos reabastecimiento
        JOIN productos producto ON reabastecimiento.producto_id = producto.id
        JOIN proveedores proveedor ON reabastecimiento.proveedor_id = proveedor.id
        WHERE reabastecimiento.eliminado = TRUE
    """, nativeQuery = true)
    List<Object[]> findAllEliminados();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE reabastecimientos
        SET eliminado = FALSE
        WHERE id = :id
    """, nativeQuery = true)
    void restaurar(@Param("id") Long id);
}
