package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedidoProductoMapping;
import com.burbujas.gestionlimpia.models.repositories.ITipoPedidoProductoMappingRepository;
import com.burbujas.gestionlimpia.models.repositories.ITipoPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoPedidoServiceImpl implements ITipoPedidoService{
    private final ITipoPedidoRepository tipoPedidoRepository;
    private final ITipoPedidoProductoMappingRepository tipoPedidoProductoMappingRepository;


    @Autowired
    public TipoPedidoServiceImpl(ITipoPedidoRepository tipoPedidoRepository, ITipoPedidoProductoMappingRepository tipoPedidoProductoMappingRepository) {
        this.tipoPedidoRepository = tipoPedidoRepository;
        this.tipoPedidoProductoMappingRepository = tipoPedidoProductoMappingRepository;
    }

    @Override
    public List<TipoPedido> findAll() {
        return this.tipoPedidoRepository.findAll();
    }

    @Override
    public TipoPedido save(TipoPedido tipoPedido) {
        this.tipoPedidoRepository.save(tipoPedido);
        return tipoPedido;
    }

    @Override
    public TipoPedido findById(Long id) {
        return this.tipoPedidoRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        this.tipoPedidoRepository.deleteById(id);
    }

    @Override
    public List<TipoPedidoProductoMapping> findAllTipoPedidoProductoMapping() {
        return this.tipoPedidoProductoMappingRepository.findAll();
    }

    @Override
    public List<TipoPedidoProductoMapping> findTipoPedidoProductoMappingByTipoPedido(TipoPedido tipoPedido) {
        return this.tipoPedidoProductoMappingRepository.findAllByTipoPedido(tipoPedido);
    }

    @Override
    public List<TipoPedidoProductoMapping> findTipoPedidoProductoMappingByProducto(Producto producto) {
        return  this.tipoPedidoProductoMappingRepository.findAllByProducto(producto);
    }

    @Override
    public void saveTipoPedidoProductoMapping(TipoPedidoProductoMapping tipoPedidoProductoMapping) {
        this.tipoPedidoProductoMappingRepository.save(tipoPedidoProductoMapping);
    }

    @Override
    public void saveAllTipoPedidoProductoMapping(List<TipoPedidoProductoMapping> tipoPedidoProductoMappings) {
        this.tipoPedidoProductoMappingRepository.saveAll(tipoPedidoProductoMappings);
    }

    @Override
    public TipoPedidoProductoMapping findTipoPedidoProductoMappingById(Long id) {
        return this.tipoPedidoProductoMappingRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTipoPedidoProductoMapping(Long id) {
        this.tipoPedidoProductoMappingRepository.deleteById(id);
    }
}
