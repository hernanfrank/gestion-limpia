package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProveedorRepository extends JpaRepository<Proveedor, Long> {
    @Query("SELECT proveedor.nombre AS nombre_proveedor, COUNT(reabastecimiento) as cantidad FROM Reabastecimiento reabastecimiento JOIN Proveedor proveedor ON proveedor = reabastecimiento.proveedor GROUP BY nombre_proveedor ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> countAllReabastecimientos();

    @Query("SELECT proveedor.nombre AS nombre_proveedor, SUM(reabastecimiento.precio) as cantidad FROM Reabastecimiento reabastecimiento JOIN Proveedor proveedor ON proveedor = reabastecimiento.proveedor GROUP BY nombre_proveedor ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> sumAllCostoReabastecimientos();
}
