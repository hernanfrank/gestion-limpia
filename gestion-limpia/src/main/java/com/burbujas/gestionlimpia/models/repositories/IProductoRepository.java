package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long> {
    @Query(value = """
            SELECT
                   producto.id,
                   producto.tipo,
                   producto.cantidad_actual,
                   producto.umbral_aviso_reabastecimiento,
                   reabastecimiento.fecha as ultimo_reabastecimiento
            FROM productos producto
            JOIN reabastecimientos reabastecimiento ON producto.id = reabastecimiento.producto_id
            WHERE producto.eliminado = true
            AND reabastecimiento.fecha = (
                SELECT MAX(r.fecha)
                FROM reabastecimientos r
                WHERE r.producto_id = producto.id
            )""", nativeQuery = true)
    List<Object[]> findAllProductosEliminados();

    Producto findByTipo(String tipo);

    @Query("SELECT producto.tipo, SUM(reabastecimiento.precio) as cantidad FROM Producto producto JOIN Reabastecimiento reabastecimiento ON producto = reabastecimiento.producto GROUP BY producto.tipo ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> sumAllUsoGroupByTipo();

    @Query("SELECT producto.tipo, COUNT(historial) as cantidad FROM Producto producto JOIN HistorialProductoPedido historial ON producto = historial.producto GROUP BY producto.tipo ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> countAllUsoGroupByTipo();

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE
                productos producto,
                reabastecimientos reabastecimiento
            SET
                producto.eliminado = false,
                reabastecimiento.eliminado = false
            WHERE producto.id = :id
                AND reabastecimiento.producto_id = producto.id
            """, nativeQuery = true)
    void restaurar(@Param("id") Long id);
}
