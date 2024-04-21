package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedidoProductoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITipoPedidoProductoMappingRepository extends JpaRepository<TipoPedidoProductoMapping, Long> {
    List<TipoPedidoProductoMapping> findAllByProducto(Producto producto);

    List<TipoPedidoProductoMapping> findAllByTipoPedido(TipoPedido tipoPedido);

    TipoPedidoProductoMapping findByTipoPedidoAndProducto(TipoPedido tipoPedido, Producto producto);
}
