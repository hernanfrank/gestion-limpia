package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.*;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import com.burbujas.gestionlimpia.models.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.*;

@Controller
public class PedidoController {

    private final IPedidoService pedidoService;
    private final ITipoPedidoService tipoPedidoService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IMaquinaService maquinaService;
    private final ICajaService cajaService;

    @Autowired
    public PedidoController(IPedidoService pedidoService, ITipoPedidoService tipoPedidoService, IClienteService clienteService, IProductoService productoService, IMaquinaService maquinaService, ICajaService cajaService) {
        this.pedidoService = pedidoService;
        this.tipoPedidoService = tipoPedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.maquinaService = maquinaService;
        this.cajaService = cajaService;
    }

    @GetMapping(value = {"/", ""})
    public String listarIndex(Model model){
        List<Pedido> pedidosPendientes = pedidoService.findAllByEstadoActualOrderByPrioridadDescFechaHoraIngresoAsc(EstadoPedido.PENDIENTE);
        Map<Integer, Pedido> lavadorasPedidosMap = new HashMap<>();
        Map<Integer, Pedido> secadorasPedidosMap = new HashMap<>();

        // obtenemos los pedidos en estado LAVADO y los mapeamos con el numero de su máquina actual
        maquinaService.findAllByTipo(TipoMaquina.LAVADORA).forEach(
                lavadora -> {
                    Pedido pedidoAsociado = pedidoService.findByMaquinaActualId(lavadora.getId());
                    lavadorasPedidosMap.put(lavadora.getNumero(), pedidoAsociado);
                }
        );
        // lo mismo con los pedidos en estado SECADO
        maquinaService.findAllByTipo(TipoMaquina.SECADORA).forEach(
                secadora -> {
                    Pedido pedidoAsociado = pedidoService.findByMaquinaActualId(secadora.getId());
                    secadorasPedidosMap.put(secadora.getNumero(), pedidoAsociado);
                }
        );

        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("lavadorasPedidosMap", lavadorasPedidosMap);
        model.addAttribute("secadorasPedidosMap", secadorasPedidosMap);
        return "index";
    }

    @GetMapping(value = {"/pedidos/nuevo/{last}"})
    public String crear(@PathVariable(value = "last") String last, Model model) {
        model.addAttribute("titulo", "Nuevo pedido");
        if(Objects.equals(last, "index")){
            model.addAttribute("last", "");
        }else{
            model.addAttribute("last", "pedidos/listar");
        }

        // instanciamos un pedido
        Pedido pedido = new Pedido();
        List<TipoPedido> tiposPedido = tipoPedidoService.findAll();
        List<Cliente> clientes = clienteService.findAll();

        // mapeamos el pedido y listado de clientes al formulario
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clientes);
        model.addAttribute("tiposPedido", tiposPedido);
        return "pedidos/pedido";
    }

    @PostMapping(value = "/pedidos/guardar")
    public String guardar(@Valid Pedido pedido, BindingResult result, Model model, RedirectAttributes flashmsg) {
        try {
            if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
                model.addAttribute("titulo", model.getAttribute("titulo"));

                List<Cliente> clientes = clienteService.findAll();
                List<TipoPedido> tiposPedido = tipoPedidoService.findAll();

                model.addAttribute("clientes", clientes);
                model.addAttribute("tiposPedido", tiposPedido);
                return "pedidos/pedido";
            }
            if (pedido.getId() != null) {// sólo si el pedido no es nuevo
                // recién acá busco el historial de estados porque sino debería buscarlo cada ves que entra a la edición el pedido, de esta forma solo lo busca cuando se guarda...
                List<HistorialEstadoPedido> historialEstadosPedido = pedidoService.getHistorialEstadosByPedidoId(pedido.getId());
                if (historialEstadosPedido != null) {
                    pedido.setHistorialEstadoPedido(historialEstadosPedido);
                }
            } else {
                List<HistorialEstadoPedido> historialInicial = new ArrayList<>();
                historialInicial.add(new HistorialEstadoPedido(pedido, EstadoPedido.INGRESADO, EstadoPedido.PENDIENTE, new Timestamp(System.currentTimeMillis())));
                pedido.setHistorialEstadoPedido(historialInicial);
            }

            // cuando se edita un pedido la descripción queda como string vacío en vez de null, asi que lo evitamos acá
            if (pedido.getDescripcion().equals("")) {
                pedido.setDescripcion(null);
            }

            // recibimos el objeto pedido del formulario y lo persistimos
            pedidoService.save(pedido);
            flashmsg.addFlashAttribute("messageType", "success");
            flashmsg.addFlashAttribute("message", "Listado de pedidos actualizado");

            // redirigimos al listado
            return "redirect:/";
        }catch (Exception e){
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "Ha ocurrido un error. Intente nuevamente.");
            return "redirect:/";
        }
    }

    @GetMapping(value = "/pedidos/listar")
    public String listar(Model model){
        model.addAttribute("titulo", "Listado de pedidos");

        List<Pedido> pedidos = pedidoService.findAllByOrderByEstadoActualDescPrioridadDescFechaHoraIngresoAsc();
        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("pedidos", pedidos);

        return "pedidos/pedidos";
    }

    @GetMapping(value = "/pedidos/listar/estado/{estadoActual}")
    public String listarConEstadoActual(@PathVariable(value = "estadoActual") EstadoPedido estadoActual, Model model){
        model.addAttribute("titulo", "Listado de pedidos");

        List<Pedido> pedidos = pedidoService.findAllByEstadoActual(estadoActual);
        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("pedidos", pedidos);

        return "pedidos/pedidos";
    }

    @GetMapping(value = "/pedidos/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Pedido pedido = pedidoService.findById(id);

        // chequeamos que se pase un id valido
        if (id > 0 && pedido != null) {
            if(pedido.getEstadoActual().equals(EstadoPedido.FINALIZADO) || pedido.getEstadoActual().equals(EstadoPedido.COBRADO)){
                flashmsg.addFlashAttribute("messageType", "error");
                flashmsg.addFlashAttribute("message", "No se pueden editar pedidos finalizados ni cobrados.");
                return "redirect:/pedidos/listar";
            }
            // obtenemos el pedido desde la bdd por id y lo pasamos a la vista
            model.addAttribute("pedido", pedido);

            List<Cliente> clientes = clienteService.findAll();
            List<TipoPedido> tiposPedido = tipoPedidoService.findAll();

            model.addAttribute("clientes", clientes);
            model.addAttribute("tiposPedido", tiposPedido);
            model.addAttribute("last", "pedidos/listar");
            model.addAttribute("titulo", "Editar pedido");
            return "pedidos/pedido";
        } else {
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "No se encontró el pedido.");
            return "redirect:/pedidos/listar";
        }

    }

    @GetMapping("/pedidos/eliminar/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable(name = "id") Long id){
        Pedido pedido = pedidoService.findById(id);
        if (pedido != null) {
            pedidoService.deleteById(id);
            // actualizo la cantidad actual para cada producto, para corregir por la eliminación de este pedido
            productoService.findAll().forEach(productoService::updateCantidadActual);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El pedido ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el pedido.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/pedidos/{idPedido}/maquina/{numeroMaquina}/{force}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> asignarPedidoAMaquina(@PathVariable(name = "idPedido") Long idPedido,
                                                        @PathVariable(name = "numeroMaquina") Integer numeroMaquina,
                                                        @PathVariable(name = "force") boolean force){
        String result = pedidoService.asignarAMaquina(idPedido, numeroMaquina, force);
        if(Objects.equals(result, "repetido")){
            // si ya pasó por el estado de esta máquina, pregunto si quiere volver a asignarlo (por si se equivocó)
            return new ResponseEntity<Object>("{\"status\":\"REPETIDO\",\"msg\": \"El pedido ya pasó por este estado anteriormente, ¿desea asignarlo igualmente?\"}", HttpStatus.OK);
        } else if(Objects.equals(result, "error")) {
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al asignar el pedido a la máquina. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // si se logró signar a la máquina, seteo el uso de ella
        if(!this.productoService.setUso(idPedido, numeroMaquina)){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al actualizar el contenido de producto usado. Revise el inventario para corregir inconsistencias.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"Pedido asignado correctamente a la máquina\"}", HttpStatus.OK);

    }

    @PostMapping(value = "/pedidos/{idPedido}/estado/{estado}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cambiarEstadoPedido(@PathVariable(name = "idPedido") Long idPedido, @PathVariable(name = "estado") String estado){
        try{
            if(!pedidoService.asignarAMaquina(idPedido, 0, estado)){
                return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al cambiar el estado del pedido. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El estado del pedido ha sido cambiado correctamente.\"}", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al cambiar el estado del pedido. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/cobrar/{idPedido}/caja/{caja}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cobrar(@PathVariable(name = "idPedido") Long idPedido, @PathVariable(name = "caja") TipoCaja caja){
        try{
            // genero el movimiento y lo asocio al pedido
            Pedido pedido = this.pedidoService.findById(idPedido);
            MovimientoCaja movimientoCobranza = this.cajaService.generarMovimientoPorCobranzaPedido(pedido,caja);

            List<MovimientoCaja> movimientosAsociadosAPedido = pedido.getMovimientosCaja();
            movimientosAsociadosAPedido.add(movimientoCobranza);
            pedido.setMovimientosCaja(movimientosAsociadosAPedido);
            pedido.setEstadoActual(EstadoPedido.COBRADO);
            this.pedidoService.save(pedido);

            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"Se ha registrado la cobranza correctamente.\"}", HttpStatus.OK);

        }catch (Exception e){
            System.out.println("Excepción capturada al cobrar un pedido: "+e);
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al generar la cobranza del pedido. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
