package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.*;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import com.burbujas.gestionlimpia.models.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class PedidoServiceImpl implements IPedidoService{

    private final IPedidoRepository pedidoRepository;
    private final IMaquinaRepository maquinaRepository;

    @Autowired
    public PedidoServiceImpl(IPedidoRepository pedidoRepository, IMaquinaRepository maquinaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.maquinaRepository = maquinaRepository;
    }

    @Override
    public List<Pedido> findAll() {
        return this.pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc() {
        // ordenamos por estado > prioridad > fecha, mandando los ya cobrados al final, asi los primeros son los próximos a cobrar
        List<Pedido> pedidos = this.pedidoRepository.findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();
        pedidos.sort((p1, p2) -> {
            int prioridadP1 = p1.getEstadoActual().obtenerPrioridad();
            int prioridadP2 = p2.getEstadoActual().obtenerPrioridad();
            return Integer.compare(prioridadP1, prioridadP2);
        });
        return pedidos;
    }

    @Override
    public List<Object[]> findAllEliminados() {
        return this.pedidoRepository.findAllEliminados();
    }

    @Override
    @Transactional
    public void restaurarPedido(Long id) throws Exception {
        try {
            Object[] cliente = this.pedidoRepository.checkClienteIsEliminadoByPedidoId(id);
            if ((boolean) cliente[0]) {
                throw new Exception("No se puede restaurar un pedido que ha sido generado por un cliente eliminado.");
            }
            this.pedidoRepository.restaurarPedido(id);
        } catch (Exception e) {
            throw new Exception("Error al restaurar el pedido N° "+id+": " + e.getMessage());
        }
    }


    @Override
    public List<Pedido> findAllByEstadoActual(EstadoPedido estadoActual) {
        return this.pedidoRepository.findAllByEstadoActual(estadoActual);
    }

    @Override
    public List<Pedido> findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(EstadoPedido estadoActual) {
        return this.pedidoRepository.findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(estadoActual);
    }

    @Override
    public List<Pedido> findAllByTipoPedido(TipoPedido tipoPedido) {
        return this.pedidoRepository.findAllByTipo(tipoPedido);
    }

    @Override
    public List<Date> findAllMonthsWithPedidosEntregados() {
        return this.pedidoRepository.findAllMonthsWithPedidosEntregados();
    }

    @Override
    public Double avgTiempoPorPedidoGroupByMes(String fecha) {
        LocalDate fechaDesde = LocalDate.parse(fecha).withDayOfMonth(1);
        LocalDate fechaHasta = fechaDesde.with(lastDayOfMonth());
        Double val = this.pedidoRepository.avgTiempoPorPedidoByMes(Timestamp.valueOf(fechaDesde.atStartOfDay()), Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));
        return val == null ? 0 : val;
    }

    @Override
    public Integer countAllInMes(String fecha) {
        LocalDate fechaDesde = LocalDate.parse(fecha).withDayOfMonth(1);
        LocalDate fechaHasta = fechaDesde.with(lastDayOfMonth());
        Integer val = this.pedidoRepository.countAllByFechaAfterAndFechaBefore(Timestamp.valueOf(fechaDesde.atStartOfDay()), Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));
        return val == null ? 0 : val;
    }

    @Override
    public List<Object[]> countAllGroupByCliente() {
        return this.pedidoRepository.countAllGroupByCliente();
    }

    @Override
    public List<Object[]> sumAllGroupByCliente() {
        return this.pedidoRepository.sumAllGroupByCliente();
    }

    @Override
    public List<Object[]> countAllGroupByMes(String fecha) {
        LocalDate fechaBusqueda = LocalDate.parse(fecha);
        return this.pedidoRepository.countAllGroupByMes(Timestamp.valueOf(fechaBusqueda.atTime(23,59,59)));
    }

    @Override
    public List<Object[]> avgTiempoByEstadoPedido() {
        return this.pedidoRepository.avgTiempoByEstadoPedido();
    }

    @Override
    public List<Object[]> avgTiempoByTipoPedido() {
        return this.pedidoRepository.avgTiempoByTipoPedido();
    }

    @Override
    public List<Object[]> countAllGroupByDiaDeSemana(String fecha) {
        LocalDate fechaBusqueda = LocalDate.parse(fecha);
        return this.pedidoRepository.countAllGroupByDiaDeSemana(Timestamp.valueOf(fechaBusqueda.atTime(23,59,59)));
    }

    @Override
    public List<HistorialEstadoPedido> getHistorialEstadosByPedidoId(Long id) {
        // si no existe el pedido retorno null
        try {
            return Objects.requireNonNull(this.pedidoRepository.findById(id).orElse(null)).getHistorialEstadoPedido();
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public List<HistorialMaquinaPedido> getHistorialMaquinasByPedidoId(Long id) {
        // si no existe el pedido retorno null
        try {
            return Objects.requireNonNull(this.pedidoRepository.findById(id).orElse(null)).getHistorialMaquinaPedido();
        } catch (NullPointerException e){
            return null;
        }
    }

    private boolean realizarAsignacion(Optional<Pedido> pedido, Optional<Maquina> maquina, EstadoPedido estado) throws Exception {
        if(pedido.isPresent() && maquina.isPresent()) {
            try{
                pedido.get().setMaquinaActual(maquina.get());
                pedido.get().setEstadoActual(estado);
                if(estado.equals(EstadoPedido.ENTREGADO)){
                    pedido.get().setFechaHoraEntrega(Timestamp.valueOf(LocalDateTime.now()));
                }
                this.save(pedido.get());
                return true;
            }catch (Exception e){
                throw new Exception("-> realizarAsignación"+e);
            }
        }else{
            // si falla la asignación retorno false
            return false;
        }
    }

    @Override
    public String asignarAMaquina(Long pedidoId, Integer maquinaNumero, boolean force) {
        try{
            // si estoy asignando a una máquina verdadera, la busco por id, y busco su estado asociado
            Optional<Pedido> pedido = this.pedidoRepository.findById(pedidoId);
            Optional<Maquina> maquina = this.maquinaRepository.findByNumero(maquinaNumero);

            if(pedido.isPresent() && maquina.isPresent()) {
                EstadoPedido estadoProximo = maquina.get().getEstadoAsociado();
                if(!force){
                    // me fijo si ya pasó por el próximo estado al que está por pasar
                    // si es true, pregunto en el front si quiere volver a asignarlo a este estado
                    // y si el usuario lo fuerza, llamo de nuevo esta función con force = true
                    AtomicBoolean estadoYaExiste = new AtomicBoolean(false);
                    pedido.get().getHistorialEstadoPedido().forEach(
                            historialEstadoPedido -> {
                                if(
                                        historialEstadoPedido.getEstadoAnterior().equals(estadoProximo) ||
                                                historialEstadoPedido.getEstadoNuevo().equals(estadoProximo)
                                ){
                                    estadoYaExiste.set(true);
                                }
                            }
                    );
                    if(estadoYaExiste.get()){
                        return "repetido";
                    }
                }
                if(realizarAsignacion(pedido, maquina, estadoProximo)) {
                    return "exito";
                }else{
                    return "error";
                }
            }else{
                return "error";
            }
        } catch (Exception e){
            return "error";
        }
    }

    @Override
    public boolean asignarAMaquina(Long pedidoId, Integer maquinaNumero, String estado) {
        try{
            // si estoy asignando a una máquina artificial (FINALIZADO, CANCELADO o PENDIENTE),
            // siempre va a ser la de tipo NINGUNO, y va a cambiar el estado asociado
            Optional<Pedido> pedido = this.pedidoRepository.findById(pedidoId);
            Optional<Maquina> maquina = Optional.ofNullable(this.maquinaRepository.findAllByTipo(TipoMaquina.NINGUNO).get(0));
            EstadoPedido estado_pedido = null;
            switch (estado){
                case "PENDIENTE" -> estado_pedido = EstadoPedido.PENDIENTE;
                case "CANCELADO" -> estado_pedido = EstadoPedido.CANCELADO;
                case "FINALIZADO" -> estado_pedido = EstadoPedido.FINALIZADO;
                case "ENTREGADO" -> estado_pedido = EstadoPedido.ENTREGADO;
            }
            return realizarAsignacion(pedido, maquina, estado_pedido);
        } catch (Exception e){
            System.out.println("-> error en asignarAMaquina "+e);
            return false;
        }
    }

    @Override
    public Pedido findById(Long id) {
        return this.pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public Pedido findByMaquinaActualId(Long maquinaId) {
        return this.pedidoRepository.findByMaquinaActualId(maquinaId);
    }

    @Override
    public void save(Pedido pedido) throws Exception {
        try {
            Timestamp ahora = new Timestamp(System.currentTimeMillis()); // para que haya consistencia en los historiales, uso la misma instancia de timestamp

            Maquina maquinaAnterior = null;
            if(!pedido.getHistorialMaquinaPedido().isEmpty()){
                // del último cambio de maquina, obtengo la maquina nueva (osea el anterior al cambio actual), si no hay cambio es pq es un pedido nuevo y por lo tanto no tiene máquina anterior
                maquinaAnterior = pedido.getHistorialMaquinaPedido().get(pedido.getHistorialMaquinaPedido().size() - 1).getMaquinaNueva();
            }
            Maquina maquinaNueva = pedido.getMaquinaActual();
            if (maquinaNueva != maquinaAnterior) {
                // si cambió la máquina, agrego al historial
                HistorialMaquinaPedido cambioMaquina = new HistorialMaquinaPedido(pedido, maquinaAnterior, maquinaNueva, ahora);
                pedido.getHistorialMaquinaPedido().add(cambioMaquina);
            }

            // También cambio el estado asociado a la maquina
            EstadoPedido estadoAnterior = EstadoPedido.INGRESADO;
            if(!pedido.getHistorialEstadoPedido().isEmpty()) { // si no había otro estado antes es pq es un pedido nuevo, por lo tanto el estado anterior es ingresado
                estadoAnterior = pedido.getHistorialEstadoPedido().get(pedido.getHistorialEstadoPedido().size() - 1).getEstadoNuevo();
            }

            // analizamos si el cambio de estado corresponde a la lógica del negocio, y sino lanzamos excepcion
            EstadoPedido estadoNuevo = pedido.getEstadoActual();
            if(estadoNuevo.equals(EstadoPedido.COBRADO) && !estadoAnterior.equals(EstadoPedido.FINALIZADO)){
                // a estado cobrado solo puede pasar si el anterior es finalizado, sino hay algún error
                throw new Exception("-> Ha ocurrido un error al pasar de FINALIZADO a COBRADO en el pedido ID " + pedido.getId());
            }else if(estadoNuevo.equals(EstadoPedido.ENTREGADO) && !estadoAnterior.equals(EstadoPedido.COBRADO)){
                // a estado entregado solo puede pasar si el anterior es cobrado, sino hay algún error
                throw new Exception("-> Ha ocurrido un error al pasar de COBRADO a ENTREGADO en el pedido ID " + pedido.getId());
            }

            if(!estadoAnterior.equals(estadoNuevo)) {
                // si cambió el estado, agrego al historial
                HistorialEstadoPedido cambioEstado = new HistorialEstadoPedido(pedido, estadoAnterior, estadoNuevo, ahora);
                pedido.getHistorialEstadoPedido().add(cambioEstado);
            }

            this.pedidoRepository.save(pedido);
        }catch (Exception e){
            throw new Exception("-> Excepción capturada al guardar un pedido: "+e);
        }
    }

    @Override
    public void deleteById(Long id) {
        this.pedidoRepository.deleteById(id);
    }
}
