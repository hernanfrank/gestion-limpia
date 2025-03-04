package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedidoProductoMapping;

import java.util.List;

public interface ITipoPedidoService {
    public List<TipoPedido> findAll();

    public TipoPedido save(TipoPedido tipoPedido);

    public TipoPedido findById(Long id);

    public void delete(Long id);

    List<TipoPedidoProductoMapping> findAllTipoPedidoProductoMapping();

    List<TipoPedidoProductoMapping> findTipoPedidoProductoMappingByTipoPedido(TipoPedido tipoPedido);

    List<TipoPedidoProductoMapping> findTipoPedidoProductoMappingByProducto(Producto producto);

    void saveTipoPedidoProductoMapping(TipoPedidoProductoMapping tipoPedidoProductoMapping);

    void saveAllTipoPedidoProductoMapping(List<TipoPedidoProductoMapping> tipoPedidoProductoMappings);

    TipoPedidoProductoMapping findTipoPedidoProductoMappingById(Long id);

    void deleteTipoPedidoProductoMapping(Long id);
}
