package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialProductoPedido;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;

import java.util.List;

public interface IProductoService {
    public List<Producto> findAll();

    public Producto findById(Long id);

    public Producto findByTipo(String tipo);

    public List<Reabastecimiento> findAllReabastecimientosByProductoId(Long id);

    public List<HistorialProductoPedido> getHistorialProductoPedidoByProductoId(Long id);

    public void save(Producto producto);

    public void delete(Long id);
}
