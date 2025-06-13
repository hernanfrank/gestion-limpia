package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long> {
    Producto findByTipo(String tipo);

    @Query("SELECT producto.tipo, SUM(reabastecimiento.precio) as cantidad FROM Producto producto JOIN Reabastecimiento reabastecimiento ON producto = reabastecimiento.producto GROUP BY producto.tipo ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> sumAllUsoGroupByTipo();

    @Query("SELECT producto.tipo, COUNT(historial) as cantidad FROM Producto producto JOIN HistorialProductoPedido historial ON producto = historial.producto GROUP BY producto.tipo ORDER BY cantidad DESC LIMIT 10")
    List<Object[]> countAllUsoGroupByTipo();
}
