package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMovimientoCaja;
import com.burbujas.gestionlimpia.models.repositories.IMovimientoCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service("CajaServiceImpl")
public class CajaServiceImpl implements ICajaService{

    private final IMovimientoCajaRepository movimientoCajaRepository;

    @Autowired
    public CajaServiceImpl(IMovimientoCajaRepository MovimientoCajaRepository) {
        this.movimientoCajaRepository = MovimientoCajaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc() {
        return this.movimientoCajaRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta) {
        return this.movimientoCajaRepository.findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(fechaDesde, fechaHasta);
    }

    @Override
    public MovimientoCaja findMovimientoCajaById(Long id) {
        return this.movimientoCajaRepository.findById(id).orElse(null);
    }

    @Override
    public List<MovimientoCaja> findAllMovimientosByTipoCaja(TipoCaja tipoCaja) {
        return this.movimientoCajaRepository.findAllByTipoCaja(tipoCaja);
    }

    @Override
    public void saveMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.movimientoCajaRepository.save(movimientoCaja);
    }

    @Override
    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.movimientoCajaRepository.delete(movimientoCaja);
    }

    @Override
    public MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento) {
        return this.movimientoCajaRepository.findMovimientoCajaByReabastecimiento(reabastecimiento);
    }

    @Override
    public MovimientoCaja generarMovimientoPorCobranzaPedido(Pedido pedido, TipoCaja tipoCaja){
        try{
            MovimientoCaja movimientoCobranza = new MovimientoCaja();
            Date ahora = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            movimientoCobranza.setDescripcion("Generado por cobranza de pedido N°"+pedido.getId()+" del cliente "+pedido.getCliente().getNombreApellido()+" en la fecha "+formatter.format(ahora)+".");
            movimientoCobranza.setFecha(ahora);
            movimientoCobranza.setTipoMovimientoCaja(TipoMovimientoCaja.INGRESO);
            movimientoCobranza.setTipoCaja(tipoCaja);
            movimientoCobranza.setMonto(pedido.getPrecio());
            movimientoCobranza.setPedido(pedido);
            this.movimientoCajaRepository.save(movimientoCobranza);
            return movimientoCobranza;
        }catch (Exception e){
            System.out.println("Excepción capturada al cobrar un pedido: "+e);
            throw e;
        }
    }
}
