package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.*;
import com.burbujas.gestionlimpia.models.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class ProductoServiceImpl implements IProductoService{

    private final IProductoRepository productoRepository;
    private final ITipoPedidoProductoMappingRepository tipoPedidoProductoMappingRepository;
    private final IReabastecimientoRepository reabastecimientoRepository;
    private final IProveedorRepository proveedorRepository;
    private final IPedidoRepository pedidoRepository;

    private final ICajaService cajaService;

    @Autowired
    public ProductoServiceImpl(IProductoRepository productoRepository, ITipoPedidoProductoMappingRepository tipoPedidoProductoMappingRepository, IReabastecimientoRepository reabastecimientoRepository, IProveedorRepository proveedorRepository, IPedidoRepository pedidoRepository, ICajaService cajaService) {
        this.productoRepository = productoRepository;
        this.tipoPedidoProductoMappingRepository = tipoPedidoProductoMappingRepository;
        this.reabastecimientoRepository = reabastecimientoRepository;
        this.proveedorRepository = proveedorRepository;
        this.pedidoRepository = pedidoRepository;
        this.cajaService = cajaService;
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
    public Double sumAllReabastecimientosInMes(String fecha) {
        LocalDate fechaDesde = LocalDate.parse(fecha).withDayOfMonth(1);
        LocalDate fechaHasta = fechaDesde.with(lastDayOfMonth());
        Double val = this.reabastecimientoRepository.sumAllByFechaAfterAndFechaBefore(Timestamp.valueOf(fechaDesde.atStartOfDay()), Timestamp.valueOf(fechaHasta.atStartOfDay()));
        return val == null ? 0.0 : val;//si es null, no hay gastos de inventario entre las fechas
    }

    @Override
    public List<Object[]> sumAllReabastecimientosGroupByMes(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha);
        return this.reabastecimientoRepository.sumAllReabastecimientosGroupByMes(Timestamp.valueOf(fechaHasta.atStartOfDay()));

    }

    @Override
    public List<Object[]> countAllReabastecimientosGroupByMes(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha);
        return this.reabastecimientoRepository.countAllReabastecimientosGroupByMes(Timestamp.valueOf(fechaHasta.atStartOfDay()));

    }

    @Override
    public List<Object[]> sumAllUsoGroupByTipo() {
        return this.productoRepository.sumAllUsoGroupByTipo();
    }

    @Override
    public List<Object[]> countAllUsoGroupByTipo() {
        return this.productoRepository.countAllUsoGroupByTipo();
    }

    @Override
    public void updateCantidadActual(Producto producto) {
        final Double[] cantidad = {0.d};
        if(producto.getReabastecimientos() != null) {
            producto.getReabastecimientos().forEach(reabastecimiento -> cantidad[0] += reabastecimiento.getCantidadProducto());
        }
        if(producto.getHistorialProductoPedidos() != null) {
            producto.getHistorialProductoPedidos().forEach(historialProductoPedido -> {
                TipoPedidoProductoMapping tipoPedidoProductoMappings = this.tipoPedidoProductoMappingRepository.findByTipoPedidoAndProducto(historialProductoPedido.getPedido().getTipo(), producto);
                cantidad[0] -= tipoPedidoProductoMappings.getCantidadUsada();
            });
        }
        producto.setCantidadActual(cantidad[0]);
        this.productoRepository.save(producto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean setUso(Long idPedido, Integer maquinaNumero){
        try {
            Pedido pedido = this.pedidoRepository.findById(idPedido).orElse(null);
            if (pedido == null) {
                return false;
            }
            // Agrego al historial el producto y descuento lo usado en cantidad actual
            List<TipoPedidoProductoMapping> tipoPedidoProductoMappings = this.tipoPedidoProductoMappingRepository.findAllByTipoPedido(pedido.getTipo());

            tipoPedidoProductoMappings.forEach(tipoPedidoProductoMapping -> {
                // agrego al historial de producto - pedido
                HistorialProductoPedido historialProductoPedido = new HistorialProductoPedido();
                historialProductoPedido.setPedido(pedido);
                historialProductoPedido.setProducto(tipoPedidoProductoMapping.getProducto());
                historialProductoPedido.setCantidadUsada(tipoPedidoProductoMapping.getCantidadUsada());
                pedido.getHistorialProductoPedido().add(historialProductoPedido);

                // descuento lo usado en cantidad actual del producto
                Double cantidadActualProducto = tipoPedidoProductoMapping.getProducto().getCantidadActual();
                tipoPedidoProductoMapping.getProducto().setCantidadActual(cantidadActualProducto - tipoPedidoProductoMapping.getCantidadUsada());
                this.productoRepository.save(tipoPedidoProductoMapping.getProducto());
            });

            return true;
        }catch (Exception e){
            System.out.println("Excepción capturada setear uso de producto: "+e);
            return false;
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
    public void delete(Long id) {
        this.productoRepository.deleteById(id);
    }

    @Override
    public void deleteReabastecimiento(Producto producto, Reabastecimiento reabastecimiento) {
        MovimientoCaja movimientoCajaAsociado = this.cajaService.findMovimientoCajaByReabastecimiento(reabastecimiento);
        this.cajaService.deleteMovimientoCaja(movimientoCajaAsociado);
        producto.getReabastecimientos().remove(reabastecimiento);
        this.reabastecimientoRepository.delete(reabastecimiento);
    }

    @Override
    public void deleteProveedor(Long id) {
        this.proveedorRepository.deleteById(id);
    }
}
