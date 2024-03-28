package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialProductoPedido;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;

import java.util.List;

public interface IProductoService {
    public List<Producto> findAll();

    public List<Proveedor> findAllProveedores();

    public Producto findById(Long id);

    public Reabastecimiento findReabastecimientoById(Long id);

    public Proveedor findProveedorById(Long id);

    public Producto findByTipo(String tipo);

    public List<Reabastecimiento> findAllReabastecimientosByProductoId(Long id);

    public List<HistorialProductoPedido> getHistorialProductoPedidoByProductoId(Long id);

    /* vuelve a calcular la cantidad como la suma de reabastecimientos menos la suma de usos porque si
     modifico un reabastecimiento se suma más de una vez a la cantidad actual...*/
    public void updateCantidadActual(Producto producto);

    public void save(Producto producto);

    public void saveReabastecimiento(Reabastecimiento reabastecimiento);

    public void saveProveedor(Proveedor proveedor);

    public void delete(Long id);

    public void deleteReabastecimiento(Producto producto, Reabastecimiento reabastecimiento);

    public void deleteProveedor(Long id);
}
