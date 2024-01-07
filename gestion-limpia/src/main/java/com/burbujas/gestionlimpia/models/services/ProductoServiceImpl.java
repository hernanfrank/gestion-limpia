package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.HistorialProductoPedido;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.repositories.IProductoRepository;
import com.burbujas.gestionlimpia.models.repositories.IReabastecimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductoServiceImpl implements IProductoService{

    private final IProductoRepository productoRepository;
    private final IReabastecimientoRepository reabastecimientoRepository;

    @Autowired
    public ProductoServiceImpl(IProductoRepository productoRepository, IReabastecimientoRepository reabastecimientoRepository) {
        this.productoRepository = productoRepository;
        this.reabastecimientoRepository = reabastecimientoRepository;
    }

    @Override
    public List<Producto> findAll() {
        return this.productoRepository.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return this.productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto findByTipo(String tipo) {
        return this.productoRepository.findByTipo(tipo);
    }

    @Override
    public List<Reabastecimiento> findAllReabastecimientosByProductoId(Long id) {
        try { // si no existe el producto retorna null
            return Objects.requireNonNull(this.productoRepository.findById(id).orElse(null)).getReabastecimientos();
        }catch(NullPointerException e){
            return null;
        }
    }

    @Override
    public List<HistorialProductoPedido> getHistorialProductoPedidoByProductoId(Long id) {
        try { // si no existe el producto retorna null
            return Objects.requireNonNull(this.productoRepository.findById(id).orElse(null)).getHistorialProductoPedidos();
        }catch(NullPointerException e){
            return null;
        }
    }

    @Override
    public void save(Producto producto) {
        this.productoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        this.productoRepository.deleteById(id);
    }
}
