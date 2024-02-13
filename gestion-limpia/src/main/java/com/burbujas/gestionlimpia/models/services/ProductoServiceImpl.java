package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.*;
import com.burbujas.gestionlimpia.models.repositories.IProductoRepository;
import com.burbujas.gestionlimpia.models.repositories.IProveedorRepository;
import com.burbujas.gestionlimpia.models.repositories.IReabastecimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductoServiceImpl implements IProductoService{

    private final IProductoRepository productoRepository;
    private final IReabastecimientoRepository reabastecimientoRepository;
    private final IProveedorRepository proveedorRepository;

    @Autowired
    public ProductoServiceImpl(IProductoRepository productoRepository, IReabastecimientoRepository reabastecimientoRepository, IProveedorRepository proveedorRepository) {
        this.productoRepository = productoRepository;
        this.reabastecimientoRepository = reabastecimientoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Producto> findAll() {
        return this.productoRepository.findAll();
    }

    @Override
    public List<Proveedor> findAllProveedores(){
        return this.proveedorRepository.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return this.productoRepository.findById(id).orElse(null);
    }

    @Override
    public Reabastecimiento findReabastecimientoById(Long id) {
        return this.reabastecimientoRepository.findById(id).orElse(null);
    }

    @Override
    public Proveedor findProveedorById(Long id) {
        return this.proveedorRepository.findById(id).orElse(null);
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
    public void saveReabastecimiento(Reabastecimiento reabastecimiento) {
        this.reabastecimientoRepository.save(reabastecimiento);
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        this.proveedorRepository.save(proveedor);
    }

    @Override
    public void addReabastecimiento(Producto producto, Reabastecimiento reabastecimiento){
        producto.getReabastecimientos().add(reabastecimiento);
        this.productoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        this.productoRepository.deleteById(id);
    }

    @Override
    public void deleteReabastecimiento(Producto producto, Reabastecimiento reabastecimiento) {
        producto.getReabastecimientos().remove(reabastecimiento);
        this.reabastecimientoRepository.delete(reabastecimiento);
    }

    @Override
    public void deleteProveedor(Long id) {
        this.proveedorRepository.deleteById(id);
    }
}
