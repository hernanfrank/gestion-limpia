package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IProveedorRepository extends JpaRepository<Proveedor, Long> {
    @Query("SELECT proveedor.nombre AS nombre_proveedor, COUNT(reabastecimiento) as cantidad FROM Reabastecimiento reabastecimiento JOIN Proveedor proveedor ON proveedor = reabastecimiento.proveedor GROUP BY nombre_proveedor ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> countAllReabastecimientos();

    @Query("SELECT proveedor.nombre AS nombre_proveedor, SUM(reabastecimiento.precio) as cantidad FROM Reabastecimiento reabastecimiento JOIN Proveedor proveedor ON proveedor = reabastecimiento.proveedor GROUP BY nombre_proveedor ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> sumAllCostoReabastecimientos();

    @Query(value= """
        SELECT proveedor.id,
               proveedor.nombre,
               proveedor.telefono,
               proveedor.direccion,
               GROUP_CONCAT(
                       CONCAT(
                           '• ',
                           DATE_FORMAT(reabastecimiento.fecha,'%Y/%m/%d'),
                           ' - ',
                           producto.tipo
                       )
                       ORDER BY reabastecimiento.fecha DESC
                       SEPARATOR '<br>'
               ) as reabastecimientos
        FROM proveedores proveedor
        LEFT JOIN reabastecimientos reabastecimiento ON reabastecimiento.proveedor_id = proveedor.id
        LEFT JOIN productos producto ON reabastecimiento.producto_id = producto.id
        WHERE proveedor.eliminado = TRUE
        GROUP BY proveedor.id, proveedor.nombre, proveedor.telefono, proveedor.direccion
    """, nativeQuery = true)
    List<Object[]> findAllEliminados();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE
            proveedores proveedor,
            reabastecimientos reabastecimiento
        SET
            proveedor.eliminado = FALSE,
            reabastecimiento.eliminado = FALSE
        WHERE proveedor.id = :id
        AND reabastecimiento.proveedor_id = proveedor.id
    """, nativeQuery = true)
    void restaurar(@Param("id") Long id);
}
