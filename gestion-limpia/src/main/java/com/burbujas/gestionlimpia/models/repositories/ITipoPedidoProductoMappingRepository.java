package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedidoProductoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITipoPedidoProductoMappingRepository extends JpaRepository<TipoPedidoProductoMapping, Long> {
    List<TipoPedidoProductoMapping> findAllByProducto(Producto producto);

    List<TipoPedidoProductoMapping> findAllByTipoPedido(TipoPedido tipoPedido);

    TipoPedidoProductoMapping findByTipoPedidoAndProducto(TipoPedido tipoPedido, Producto producto);
}
